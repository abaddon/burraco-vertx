import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("kapt")
}

group = "com.abaddon83.vertx.eventStore"
version = "1.0-SNAPSHOT"

val kotlinVersion = ext.get("kotlinVersion")
val vertxVersion = ext.get("vertxVersion")
val junitJupiterVersion = ext.get("junitJupiterVersion")
val ktormVersion = ext.get("ktormVersion")
val mysqlConnectorVersion = ext.get("mysqlConnectorVersion")
val jacksonModuleKotlinVersion = "2.12.3"

val mainVerticleName = "com.abaddon83.vertx.eventStore.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
val launcherClassName = "com.abaddon83.vertx.eventStore.Starter"


application {
    mainClassName = launcherClassName
}

dependencies {
    implementation(project(":SharedComponents","default"))

    //Ktorm
    implementation("org.ktorm:ktorm-core:${ktormVersion}")

    //Mysql
    implementation("mysql:mysql-connector-java:${mysqlConnectorVersion}")

    //Vertx
    implementation("io.vertx:vertx-config:$vertxVersion")
    implementation("io.vertx:vertx-kafka-client:$vertxVersion")
    implementation("io.vertx:vertx-service-proxy:$vertxVersion")
    "kapt"("io.vertx:vertx-codegen:$vertxVersion:processor")
    compileOnly("io.vertx:vertx-codegen:$vertxVersion")
    implementation("io.vertx:vertx-circuit-breaker:$vertxVersion")
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")
    implementation("io.vertx:vertx-web-api-contract:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
    implementation("io.vertx:vertx-hazelcast:$vertxVersion")

    //Json
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"


tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
        )
    }
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}

kapt {
    arguments {
        arg("codegen.output", "$projectDir/src/main/generated")
    }
}