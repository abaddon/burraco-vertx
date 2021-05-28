import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
}

group = "com.abaddon83.burraco.readModel"
version = "1.0-SNAPSHOT"


val kotlinVersion = ext.get("kotlinVersion")
val vertxVersion = ext.get("vertxVersion")
val junitJupiterVersion = ext.get("junitJupiterVersion")
val ktormVersion = ext.get("ktormVersion")
val mysqlConnectorVersion = ext.get("mysqlConnectorVersion")

val mainVerticleName = "com.abaddon83.burraco.readModel.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"

application {
    mainClassName = mainVerticleName
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

    //Ktorm
    implementation("org.ktorm:ktorm-core:${ktormVersion}")
    implementation("org.ktorm:ktorm-support-mysql:${ktormVersion}")

    //Mysql
    implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")

    //Use the Kotlin JUnit integration.
    testImplementation("io.vertx:vertx-junit5:$vertxVersion")
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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}