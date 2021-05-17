plugins {
    kotlin("jvm") version "1.4.10"
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

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply(plugin = "org.jetbrains.kotlin.jvm")

//    group = "de.bentolor.sampleproject"
//    version = "0.1.0"
    //val compileKotlin: KotlinCompile by tasks

    repositories {
        mavenCentral()
        jcenter()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    dependencies {
        val implementation by configurations
        val testImplementation by configurations

        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
        //implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

        //Log
        implementation("org.slf4j:slf4j-api:1.7.25")
        implementation("org.slf4j:slf4j-log4j12:1.7.25")

        //Use the Kotlin JUnit integration.
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
        testImplementation("junit:junit:4.12") // JVM dependency
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")
    }


//    configure<JavaPluginExtension> {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }

//    configure<PublishingExtension> {
//        publications {
//            create<MavenPublication>(project.name) {
//                from(components["java"])
//
//                // If you configured them before
//                // val sourcesJar by tasks.getting(Jar::class)
//                // val javadocJar by tasks.getting(Jar::class)
//
//                val sourcesJar by tasks.creating(Jar::class) {
//                    val sourceSets: SourceSetContainer by project
//
//                    from(sourceSets["main"].allJava)
//                    classifier = "sources"
//                }
//                val javadocJar by tasks.creating(Jar::class) {
//                    from(tasks.get("javadoc"))
//                    classifier = "javadoc"
//                }
//
//                artifact(sourcesJar)
//                artifact(javadocJar)
//            }
//        }
//    }
}