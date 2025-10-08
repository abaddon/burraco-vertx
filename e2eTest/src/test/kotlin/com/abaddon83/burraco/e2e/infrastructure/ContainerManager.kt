package com.abaddon83.burraco.e2e.infrastructure

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Manages the lifecycle of Docker containers using docker-compose CLI directly.
 * This approach is simpler and more reliable than Testcontainers' DockerComposeContainer.
 */
object ContainerManager {
    private const val GAME_PORT = 8081
    private const val PLAYER_PORT = 8082
    private const val KAFKA_PORT = 19092
    private const val EVENTSTORE_PORT = 2113

    private var started = false

    /**
     * Start all containers defined in docker-compose.yml
     */
    fun start() {
        println("🚀 Starting Burraco services with docker-compose...")

        // Navigate to project root (one level up from e2eTest)
        val projectRoot = File("..").canonicalFile
        val composeFile = File(projectRoot, "docker-compose.yml")

        if (!composeFile.exists()) {
            throw IllegalStateException("docker-compose.yml not found at: ${composeFile.absolutePath}")
        }

        println("📁 Using docker-compose file: ${composeFile.absolutePath}")

        // Start services using docker compose
        val startProcess = ProcessBuilder("docker", "compose", "up", "-d")
            .directory(projectRoot)
            .redirectErrorStream(true)
            .start()

        val startOutput = startProcess.inputStream.bufferedReader().readText()
        val startExitCode = startProcess.waitFor()

        if (startExitCode != 0) {
            println("❌ Docker compose failed with exit code $startExitCode")
            println(startOutput)
            throw IllegalStateException("Failed to start docker compose services")
        }

        println("⏳ Waiting for services to be ready...")

        // Wait for services to be healthy
        waitForHealthCheck("http://localhost:$GAME_PORT/health", "Game", Duration.ofMinutes(3))
        waitForHealthCheck("http://localhost:$PLAYER_PORT/health", "Player", Duration.ofMinutes(3))

        // Store fixed endpoints (using localhost since we're not in containers)
        val endpoints = ServiceEndpoints(
            gameServiceUrl = "http://localhost:$GAME_PORT",
            playerServiceUrl = "http://localhost:$PLAYER_PORT",
            kafkaBroker = "localhost:$KAFKA_PORT",
            eventStoreUrl = "http://localhost:$EVENTSTORE_PORT"
        )

        ServiceEndpoints.set(endpoints)
        started = true

        println("✅ All services started successfully!")
        println("   Game Service: ${endpoints.gameServiceUrl}")
        println("   Player Service: ${endpoints.playerServiceUrl}")
        println("   Kafka Broker: ${endpoints.kafkaBroker}")
        println("   EventStore: ${endpoints.eventStoreUrl}")
    }

    /**
     * Stop all containers
     */
    fun stop() {
//        if (started) {
//            println("🛑 Stopping Burraco services...")
//
//            val projectRoot = File("..").canonicalFile
//            val stopProcess = ProcessBuilder("docker", "compose", "down")
//                .directory(projectRoot)
//                .redirectErrorStream(true)
//                .start()
//
//            stopProcess.waitFor(30, TimeUnit.SECONDS)
//            started = false
//            println("✅ All services stopped.")
//        }
    }

    /**
     * Wait for a health check endpoint to return 200
     */
    private fun waitForHealthCheck(url: String, serviceName: String, timeout: java.time.Duration) {
        val startTime = System.currentTimeMillis()
        val timeoutMillis = timeout.toMillis()

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 2000
                connection.readTimeout = 2000

                val responseCode = connection.responseCode
                connection.disconnect()

                if (responseCode == 200) {
                    println("   ✓ $serviceName is healthy")
                    return
                }
            } catch (e: Exception) {
                // Service not ready yet, continue waiting
            }

            Thread.sleep(2000)
        }

        throw IllegalStateException("$serviceName health check failed after ${timeout.toMillis()}ms")
    }
}
