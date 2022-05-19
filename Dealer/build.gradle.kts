plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.shadowPlugin)
    application
}

application {
    mainClass = "com.abaddon83.burraco.dealer.MainVerticle"
}

dependencies {

    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.dealer)
    implementation(project(":Common"))
    implementation(project(":KafkaAdapter"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.dealerTest)
}

tasks {
    shadowJar {
        archiveBaseName.set("Dealer")
        archiveClassifier.set("all")
        archiveVersion.set("1.0-SNAPSHOT")
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}