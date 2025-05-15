import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.shadowPlugin)
    application
}

// Custom task to calculate version
abstract class GitVersionTask : DefaultTask() {
    @get:OutputFile
    abstract val versionFile: RegularFileProperty

    @get:Input
    abstract val fallbackVersion: Property<String>

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun calculateVersion() {
        val version = try {
            val gitDescribe = runCommand("git", "describe", "--tags", "--exact-match")
            val isDirty = runCommand("git", "status", "--porcelain")?.isNotEmpty()?:false
            val lastTag = runCommand("git", "describe", "--tags", "--abbrev=0")

            when {
                gitDescribe != null && !isDirty -> gitDescribe
                lastTag != null -> "$lastTag-SNAPSHOT"
                else -> fallbackVersion.get()
            }
        } catch (e: Exception) {
            fallbackVersion.get()
        }

        versionFile.get().asFile.writeText(version)
    }

    private fun runCommand(vararg command: String): String? {
        return try {
            val process = ProcessBuilder(*command).start()
            val result = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()
            if (process.exitValue() == 0) result else null
        } catch (e: Exception) {
            null
        }
    }
}

// Register the version task
val gitVersionTask by tasks.registering(GitVersionTask::class) {
    versionFile.set(layout.buildDirectory.file("version.txt"))
    fallbackVersion.set("0.0.1-SNAPSHOT")
}

// Create a provider for the version that reads from the file
val projectVersion = gitVersionTask.flatMap { task ->
    providers.fileContents(task.versionFile).asText
}

// Set version using a default for configuration time
version = "dev-SNAPSHOT"

application {
    mainClass = "com.abaddon83.burraco.game.MainVerticle"
}

dependencies {

    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.game)
    implementation(project(":Common"))
    implementation(project(":KafkaAdapter"))

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.gameTest)
}

tasks {
    shadowJar {
        inputs.files(gitVersionTask)
        archiveBaseName.set("Game")
        archiveClassifier.set("all")
        archiveVersion.set(projectVersion)
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
}