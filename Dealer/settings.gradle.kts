rootProject.name = "Dealer"

pluginManagement{
    plugins{
        kotlin("jvm") version "1.7.10"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("com.palantir.git-version") version "2.0.0"
    }
}

include(":KafkaAdapter")
project(":KafkaAdapter").projectDir = File(settingsDir, "../KafkaAdapter")

include(":Common")
project(":Common").projectDir = File(settingsDir, "../Common")