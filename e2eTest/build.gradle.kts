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
