plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.git-version")
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

// Get the git version provider from the convention plugin
val gitVersion = extra["gitVersion"] as Provider<String>

tasks {
    shadowJar {
        archiveBaseName.set("Dealer")
        archiveClassifier.set("all")
        archiveVersion.set(gitVersion)
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
    
    register("printDealerVersion") {
        doLast {
            println("Dealer module version: ${gitVersion.get()}")
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}