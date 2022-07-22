group = "com.abaddon83.burraco.common.models"

object Versions {
    const val kcqrsCoreVersion="0.0.7"
    const val kcqrsTestVersion="0.0.10"
    const val kcqrsEventStoreDBVersion="0.0.7"
    const val slf4jVersion = "1.7.25"
    const val kotlinVersion = "1.7.10"
    const val kotlinCoroutineVersion = "1.6.0"
    const val vertxVersion = "4.3.2"
    const val jacksonModuleKotlinVersion = "2.13.0"
    const val junitJupiterVersion = "5.7.0"
    const val jacocoToolVersion = "0.8.7"
    const val jvmTarget = "11"
    const val ktormVersion ="3.2.0"
    const val mysqlConnectorVersion = "8.0.21"
    const val config4k = "0.4.2"
    const val log4jVersion= "2.17.2"
    const val testcontainers="1.17.3"
    const val hopliteVersion="2.3.3"
    const val jacksonVersion="2.13.3"
}

val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm")
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("com.github.johnrengelman.shadow")
    id("com.palantir.git-version")
    jacoco
}

val gitVersion: groovy.lang.Closure<String> by extra
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()

val lastTag=details.lastTag.substring(1)
val snapshotTag= {
    println(lastTag)
    val list=lastTag.split(".")
    val third=(list.last().toInt() + 1).toString()
    "${list[0]}.${list[1]}.$third-SNAPSHOT"
}
version = if(details.isCleanTag) lastTag else snapshotTag()


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //Config
//    implementation("com.sksamuel.hoplite:hoplite-core:${Versions.hopliteVersion}")
//    implementation("com.sksamuel.hoplite:hoplite-yaml:${Versions.hopliteVersion}")

    //kcqrs
    implementation("io.github.abaddon.kcqrs:kcqrs-core:0.0.7")
//    implementation("io.github.abaddon.kcqrs:kcqrs-EventStoreDB:${Versions.kcqrsEventStoreDBVersion}")

    //Vertx
    implementation("io.vertx:vertx-config:4.3.1")
    implementation("io.vertx:vertx-web-openapi:4.3.1")
    implementation("io.vertx:vertx-lang-kotlin:4.3.1")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:4.3.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    //Vertx HealthCheck
//    implementation("io.vertx:vertx-health-check:${Versions.vertxVersion}")
    //Vertx kafka
//    implementation("io.vertx:vertx-kafka-client:${Versions.vertxVersion}")
    //Vertx event bus tcp bridge
//    implementation("io.vertx:vertx-tcp-eventbus-bridge:${Versions.vertxVersion}")
    //Vertx service discovery
    implementation("io.vertx:vertx-service-discovery:4.3.1")

    //Logs
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")

    //Test
//    testImplementation("io.github.abaddon.kcqrs:kcqrs-test:${Versions.kcqrsTestVersion}")
    testImplementation("io.vertx:vertx-junit5:4.3.1")
//    testImplementation("io.vertx:vertx-web-client:${Versions.vertxVersion}")
//    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
//    testImplementation("org.testcontainers:kafka:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:1.17.2")


}

jacoco {
    toolVersion = Versions.jacocoToolVersion
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = Versions.jvmTarget
    }
}

//tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
//    archiveClassifier.set("all")
//    manifest {
//        attributes(mapOf(
//            "Main-Verticle" to mainVerticleName
//            ))
//    }
//    mergeServiceFiles {
//        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
//    }
//}

tasks.withType<Test> {
    useJUnitPlatform()
//    testLogging {
//        events = setOf(PASSED, SKIPPED, FAILED)
//    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
