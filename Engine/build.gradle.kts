import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    `java-library`
    application
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "5.2.0"

}

group = "com.abaddon83.vertx.burraco.engine"
version = "1.0-SNAPSHOT"



//repositories {
//    mavenCentral()
//    jcenter()
//}

val kotlinVersion = "1.4.10"
val vertxVersion = "3.9.3"
val junitJupiterVersion = "5.6.0"

val mainVerticleName = "com.abaddon83.vertx.burraco.engine.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
val launcherClassName = "com.abaddon83.vertx.burraco.engine.Starter"


application {
    mainClassName = launcherClassName
}

dependencies {
    implementation(project(":SharedComponents","default"))

    //Vertx
    implementation("io.vertx:vertx-config:$vertxVersion")
    implementation("io.vertx:vertx-web-client:$vertxVersion")
    //implementation("io.vertx:vertx-auth-jwt:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-service-proxy:$vertxVersion")
    //implementation("io.vertx:vertx-circuit-breaker:$vertxVersion")
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")
    implementation("io.vertx:vertx-web-api-contract:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion") //?
    implementation("io.vertx:vertx-hazelcast:$vertxVersion")

//    //Kotlin
//    implementation(kotlin("stdlib-jdk8"))
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
//
//    //Log
//    implementation("org.slf4j:slf4j-api:1.7.25")
//    implementation("org.slf4j:slf4j-log4j12:1.7.25")
//
//    //Use the Kotlin JUnit integration.
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
//    testImplementation("junit:junit:4.12") // JVM dependency
//    testImplementation("org.jetbrains.kotlin:kotlin-test")
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf(
            "Main-Verticle" to mainVerticleName
            ))
    }
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}

tasks.withType<Test> {
    //useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")//, "-javaagent=./quasar-core-0.8.0.jar")
}

sourceSets {
    main {
        java.srcDir("src/core/java")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}