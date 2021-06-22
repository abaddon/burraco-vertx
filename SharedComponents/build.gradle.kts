
plugins {
    id("com.github.johnrengelman.shadow") apply false
}

group = "com.abaddon83.burraco.models.common"
version = "1.0-SNAPSHOT"

val kotlinVersion = ext.get("kotlinVersion")
val vertxVersion = ext.get("vertxVersion")
val junitJupiterVersion = ext.get("junitJupiterVersion")

//repositories {
//    mavenCentral()
//    jcenter()
//}

dependencies {
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")

    implementation("io.vertx:vertx-hazelcast:$vertxVersion")
    testImplementation("io.vertx:vertx-junit5:$vertxVersion")
}
