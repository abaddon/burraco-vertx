import java.time.Duration

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // E2E test dependencies
    testImplementation(libs.bundles.e2eTest)
    testImplementation(libs.bundles.logs)

    // JUnit Platform for running Cucumber tests
    testImplementation(kotlin("test"))
    testImplementation("org.junit.platform:junit-platform-suite-api:1.11.2")
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.11.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/**
 * Task to clean up all Docker images and containers related to the Burraco project.
 * This ensures tests always run with the latest built code.
 */
tasks.register<Exec>("cleanDockerImages") {
    group = "e2e"
    description = "Remove all Burraco Docker images to force rebuild"

    // Navigate to project root for docker commands
    workingDir = project.rootDir

    // Stop and remove all containers
    doFirst {
        println("🧹 Cleaning Docker environment...")
        println("   Stopping containers...")
    }

    commandLine("docker", "compose", "down", "--volumes", "--remove-orphans")

    doLast {
        println("   Removing Docker images...")

        // Remove service images (force remove, ignore errors if images don't exist)
        val imagesToRemove = listOf(
            "burraco-vertx-game",
            "burraco-vertx-player",
            "burraco-vertx-dealer",
            "burraco-vertx-base-builder",
            "base-builder"
        )

        imagesToRemove.forEach { imageName ->
            try {
                val result = ProcessBuilder("docker", "rmi", "-f", imageName)
                    .directory(project.rootDir)
                    .start()
                    .waitFor()
                if (result != 0 && result != 1) {
                    println("   (Image $imageName removal returned code $result)")
                }
            } catch (e: Exception) {
                // Image doesn't exist, that's fine
                println("   (Image $imageName not found, skipping)")
            }
        }

        // Also remove dangling images from our project
        try {
            ProcessBuilder("docker", "image", "prune", "-f", "--filter", "label=project=burraco")
                .directory(project.rootDir)
                .start()
                .waitFor()
        } catch (e: Exception) {
            // Ignore errors
        }

        println("✅ Docker environment cleaned!")
    }
}

/**
 * Task to build all service JARs before Docker image creation
 */
tasks.register("buildServiceJars") {
    group = "e2e"
    description = "Build all service shadow JARs"

    dependsOn(":Common:build", ":KafkaAdapter:build")
    dependsOn(":Game:shadowJar", ":Player:shadowJar", ":Dealer:shadowJar")

    doLast {
        println("✅ All service JARs built successfully!")
    }
}

/**
 * Task to build Docker images with no cache
 */
tasks.register<Exec>("buildDockerImages") {
    group = "e2e"
    description = "Build all Docker images from scratch (no cache)"

    dependsOn("cleanDockerImages", "buildServiceJars")

    // Navigate to project root for docker commands
    workingDir = project.rootDir

    // Build with --no-cache to ensure fresh builds
    commandLine("docker", "compose", "build", "--no-cache")

    doFirst {
        println("🐳 Building Docker images from scratch...")
        println("   This may take several minutes...")
    }

    doLast {
        println("✅ All Docker images built successfully!")
    }
}

/**
 * Custom test task that ensures fresh Docker images
 */
tasks.register<Test>("e2eTestClean") {
    group = "e2e"
    description = "Run E2E tests with freshly built Docker images"

    dependsOn("buildDockerImages")

    // Configure like the normal test task
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    systemProperty("testcontainers.reuse.enable", "false")
    timeout.set(Duration.ofMinutes(20)) // Extra time for image building

    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    doFirst {
        println("🧪 Starting E2E tests with fresh Docker images...")
    }
}

tasks.test {
    useJUnitPlatform()

    // Cucumber configuration
    systemProperty("cucumber.junit-platform.naming-strategy", "long")

    // Testcontainers configuration
    systemProperty("testcontainers.reuse.enable", "false")

    // Set test timeout to allow time for container startup
    timeout.set(Duration.ofMinutes(15))
}

// Configuration for integration tests
configurations {
    testImplementation {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
