import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    kotlin("jvm") version "1.4.10"
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("kapt").version("1.4.10")
    kotlin("plugin.serialization") version "1.4.10"
}

group = "com.abaddon83.vertx.eventStore"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val kotlinVersion = "1.4.10"
val vertxVersion = "3.9.3"
val junitJupiterVersion = "5.6.0"
val ktormVersion = "3.2.0"

val mainVerticleName = "com.abaddon83.vertx.eventStore.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"
//val launcherClassName = "io.vertx.core.Launcher"
val launcherClassName = "com.abaddon83.vertx.eventStore.Starter"


application {
    mainClassName = launcherClassName
}

dependencies {
    implementation(project(":SharedComponents","default"))
    implementation("mysql:mysql-connector-java:8.0.21")
    //compile group: 'mysql', name: 'mysql-connector-java', version: '8.0.21'

    implementation("org.ktorm:ktorm-core:${ktormVersion}")
    implementation("io.vertx:vertx-config:$vertxVersion")
    //implementation("io.vertx:vertx-web-client:$vertxVersion")
    //implementation("io.vertx:vertx-auth-jwt:$vertxVersion")
    //implementation("io.vertx:vertx-web:$vertxVersion")


    implementation("io.vertx:vertx-kafka-client:$vertxVersion")

    implementation("io.vertx:vertx-service-proxy:$vertxVersion")
    "kapt"("io.vertx:vertx-codegen:$vertxVersion:processor")
    compileOnly("io.vertx:vertx-codegen:$vertxVersion")


    //implementation("io.vertx:vertx-service-proxy:$vertxVersion")
    //annotationProcessor("io.vertx:vertx-codegen:$vertxVersion:processor")

    implementation("io.vertx:vertx-circuit-breaker:$vertxVersion")
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")
    implementation("io.vertx:vertx-web-api-contract:$vertxVersion")

    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

    implementation("io.vertx:vertx-hazelcast:$vertxVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")

    testImplementation("io.vertx:vertx-junit5:$vertxVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
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

//tasks.register<JavaCompile>("annotationProcessing" ) { // codegen
//    group = "build"
//    source = sourceSets.main.get().java
//    classpath = configurations.compile.get() + configurations.compileOnly.get()
//    destinationDir = project.file("src/main/generated")
//    options.compilerArgs = listOf(
//        "-proc:only",
//        "-processor", "io.vertx.codegen.CodeGenProcessor",
//        "-Acodegen.output=${project.projectDir}/src/main"
//    )
//}

//compileJava {
//    targetCompatibility = 1.8
//    sourceCompatibility = 1.8
//
//    dependsOn annotationProcessing
//}

//sourceSets {
//    main {
//        java {
//            setSrcDirs(srcDirs.plus("src/main/generated"))
//        }
//    }
//}