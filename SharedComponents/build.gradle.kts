import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    kotlin("jvm") version "1.4.10"
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "com.abaddon83.burraco.models.common"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val kotlinVersion = "1.4.10"
val vertxVersion = "3.9.3"
val junitJupiterVersion = "5.6.0"

val mainVerticleName = "com.abaddon83.vertx.burraco.engine.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
//val launcherClassName = "io.vertx.core.Launcher"
val launcherClassName = "com.abaddon83.vertx.burraco.engine.Starter"

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation("junit:junit:4.12") // JVM dependency
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
