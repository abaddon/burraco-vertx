group = "com.abaddon83.burraco.dealer"

object Versions {
    const val kcqrsCoreVersion="0.0.7"
    const val kcqrsTestVersion="0.0.10"
    const val kcqrsEventStoreDBVersion="0.0.8"
    const val kotlinCoroutineVersion = "1.6.4"
    const val vertxVersion = "4.3.2"
    const val jacocoToolVersion = "0.8.7"
    const val jvmTarget = "11"
    const val log4jVersion= "2.17.2"
    const val testContainerVersion="1.17.3"
    const val hopliteVersion="2.3.3"
}

val mainVerticleName = "com.abaddon83.burraco.dealer.MainVerticle"
val watchForChange = "src/**/*"
val doOnChange = "./gradlew classes"

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.palantir.git-version")
    jacoco
    application
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

application {
    mainClass.set(mainVerticleName)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    api(project(":Common"))
    api(project(":KafkaAdapter"))

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    //Config
    implementation("com.sksamuel.hoplite:hoplite-core:${Versions.hopliteVersion}")
    implementation("com.sksamuel.hoplite:hoplite-yaml:${Versions.hopliteVersion}")

    //kcqrs
    implementation("io.github.abaddon.kcqrs:kcqrs-core:${Versions.kcqrsCoreVersion}")
    implementation("io.github.abaddon.kcqrs:kcqrs-EventStoreDB:${Versions.kcqrsEventStoreDBVersion}")

    //Vertx
    implementation("io.vertx:vertx-config:${Versions.vertxVersion}")
    implementation("io.vertx:vertx-web-openapi:${Versions.vertxVersion}")
    implementation("io.vertx:vertx-lang-kotlin:${Versions.vertxVersion}")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:${Versions.vertxVersion}")
    //Vertx HealthCheck
    implementation("io.vertx:vertx-health-check:${Versions.vertxVersion}")
    //Vertx kafka
    implementation("io.vertx:vertx-kafka-client:${Versions.vertxVersion}")
    //Vertx event bus tcp bridge
    implementation("io.vertx:vertx-tcp-eventbus-bridge:${Versions.vertxVersion}")
    //Vertx service discovery
    implementation("io.vertx:vertx-service-discovery:${Versions.vertxVersion}")

    //Logs
    implementation("org.apache.logging.log4j:log4j-api:${Versions.log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${Versions.log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${Versions.log4jVersion}")

    //Test
    testImplementation("io.github.abaddon.kcqrs:kcqrs-test:${Versions.kcqrsTestVersion}")
    testImplementation("io.vertx:vertx-junit5:${Versions.vertxVersion}")
    testImplementation("io.vertx:vertx-web-client:${Versions.vertxVersion}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutineVersion}")
    testImplementation("org.testcontainers:testcontainers:${Versions.testContainerVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testContainerVersion}")
    testImplementation("org.testcontainers:kafka:${Versions.testContainerVersion}")
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

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("all")
    manifest {
        attributes(mapOf(
            "Main-Class" to "io.vertx.core.Starter",
            "Main-Verticle" to mainVerticleName,
            "Multi-Release" to "true"
        ))
    }
//    mergeServiceFiles {
//        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
//    }
}

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