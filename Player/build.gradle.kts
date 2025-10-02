plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.git-version")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.shadowPlugin)
}

dependencies {

    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.logs)
    implementation(libs.bundles.player)
    implementation(project(":Common"))
    implementation(project(":KafkaAdapter"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.playerTest)
    testImplementation(libs.bundles.logs)
}

// Get the git version provider from the convention plugin
val gitVersion = extra["gitVersion"] as Provider<String>

tasks {
    shadowJar {
        archiveBaseName.set("Player")
        archiveClassifier.set("all")
        archiveVersion.set(gitVersion)
        manifest {
            attributes["Main-Class"] = "com.abaddon83.burraco.player.MainVerticle"
        }
    }

    register("printPlayerVersion") {
        doLast {
            println("Player module version: ${gitVersion.get()}")
        }
    }
}