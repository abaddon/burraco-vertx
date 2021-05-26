plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0" apply false
    id("com.github.johnrengelman.shadow") version "5.2.0" apply false
}
//
//group = "com.abaddon83.vertx"
//version = "1.0-SNAPSHOT"
//
repositories {
    mavenCentral()
}
//
//dependencies {
//    implementation(kotlin("stdlib"))
//}

val kotlinVersion = "1.5.10"
val kotlinCoroutineVersion = "1.3.9"
val kotlinxSerializationJson = "1.0.1"
val vertxVersion = "4.0.3"
val junitJupiterVersion = "5.7.0"
val ktormVersion ="3.2.0"
val slf4jVersion ="1.7.25"
val mysqlConnectorVersion = "8.0.21"

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.github.johnrengelman.shadow")

//    group = "de.bentolor.sampleproject"
//    version = "0.1.0"
    //val compileKotlin: KotlinCompile by tasks


    repositories {
        mavenCentral()
        jcenter()
    }

    ext.set("kotlinVersion",kotlinVersion)
    ext.set("vertxVersion",vertxVersion)
    ext.set("junitJupiterVersion",junitJupiterVersion)
    ext.set("ktormVersion",ktormVersion)
    ext.set("slf4jVersion",slf4jVersion)
    ext.set("kotlinCoroutineVersion",kotlinCoroutineVersion)
    ext.set("mysqlConnectorVersion",mysqlConnectorVersion)


    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        val implementation by configurations
        val testImplementation by configurations

        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")

        //Log
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")

        //Use the Kotlin JUnit integration.
        testImplementation("org.junit.jupiter:junit-jupiter:${junitJupiterVersion}") // JVM dependency
        //testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutineVersion")
    }
}