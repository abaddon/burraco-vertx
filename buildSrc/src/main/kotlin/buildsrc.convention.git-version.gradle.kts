// This convention plugin applies git-based versioning to a project

// Get the shared git version provider from the root project
val gitVersionProvider = rootProject.extensions.findByName("gitVersionProvider") as? Provider<String>
    ?: throw GradleException("Git version provider not found. Make sure the root project configures git versioning.")

// Set the project version
version = gitVersionProvider.getOrElse("dev-SNAPSHOT")

// Provide the version as an extra property for tasks
extra["gitVersion"] = gitVersionProvider
