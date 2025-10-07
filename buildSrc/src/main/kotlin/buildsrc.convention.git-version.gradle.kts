// This convention plugin applies git-based versioning to a project

// Get the shared git version provider from the root project
val gitVersionProvider =  provider { "dev-SNAPSHOT" }

// Set the project version
version = gitVersionProvider.get()
//version = "dev-SNAPSHOT"

// Provide the version as an extra property for tasks
extra["gitVersion"] = gitVersionProvider
