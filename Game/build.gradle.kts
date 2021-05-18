import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
}

group = "com.abaddon83.vertx.burraco.game"
version = "1.0-SNAPSHOT"


val kotlinVersion = ext.get("kotlinVersion")
val vertxVersion = ext.get("vertxVersion")
val junitJupiterVersion = ext.get("junitJupiterVersion")

val mainVerticleName = "com.abaddon83.vertx.burraco.game.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
val launcherClassName = "com.abaddon83.vertx.burraco.game.Starter"


application {
    mainClassName = launcherClassName
}

dependencies {
    implementation(project(":SharedComponents","default"))

    //Vertx
    implementation("io.vertx:vertx-config:$vertxVersion")
    implementation("io.vertx:vertx-web-openapi:$vertxVersion")
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion") //?
    implementation("io.vertx:vertx-hazelcast:$vertxVersion")
    implementation("io.vertx:vertx-kafka-client:$vertxVersion")
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