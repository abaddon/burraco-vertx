rootProject.name = "Common"

pluginManagement{
    plugins{
        kotlin("jvm") version "1.7.10"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("com.palantir.git-version") version "0.15.0"
    }
}