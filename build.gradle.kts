import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

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
            val isDirty = runCommand("git", "status", "--porcelain")?.isNotEmpty()?: false
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

// Register the version task at the root level
val gitVersionTask by tasks.registering(GitVersionTask::class) {
    versionFile.set(layout.buildDirectory.file("git-version.txt"))
    fallbackVersion.set("0.0.1-SNAPSHOT")
}

// Create a provider for the version that reads from the file
val gitVersionProvider = gitVersionTask.flatMap { task ->
    providers.fileContents(task.versionFile).asText
}

// Make the provider available to all subprojects
rootProject.extensions.add("gitVersionProvider", gitVersionProvider)

// Apply common configuration to all subprojects
subprojects {
    // Ensure the git version is calculated before any build
    tasks.configureEach {
        if (name == "build" || name == "shadowJar") {
            dependsOn(rootProject.tasks.named("gitVersionTask"))
        }
    }
}
