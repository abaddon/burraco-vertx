package com.abaddon83.burraco.game.adapters.commandController.rest

import com.abaddon83.burraco.common.vertx.AbstractHttpServiceVerticle
import com.abaddon83.burraco.game.HealthCheck
import com.abaddon83.burraco.game.ServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.AddPlayerRoutingHandler
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.NewGameRoutingHandler
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.PickUpCardRoutingHandler
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.RequestDealCardsRoutingHandler
import com.abaddon83.burraco.game.ports.CommandControllerPort
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.vertx.core.Promise
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.openapi.RouterBuilder
import java.io.File


class RestHttpServiceVerticle(
    private val serviceConfig: ServiceConfig,
    private var commandController: CommandControllerPort
) : AbstractHttpServiceVerticle() {


    override fun start(startFuture: Promise<Void>?) {
        startHttpServer(startFuture);
    }

    private fun startHttpServer(startPromise: Promise<Void>?) {
        val restHttpServiceConfig = serviceConfig.restHttpService
        val healthCheck = HealthCheck(vertx)
        RouterBuilder.create(vertx, extractAndLoadOpenAPISpec(restHttpServiceConfig.openApiPath))
            .onFailure { ex ->
                log.error("OpenApi not loaded", ex)
                startPromise?.fail(ex)
            }
            .onSuccess { routerBuilder ->
                routerBuilder.operation("healthCheck").handler(healthCheck.build())
                routerBuilder.operation("newGame").handler(NewGameRoutingHandler(commandController))
                routerBuilder.operation("addPlayer").handler(AddPlayerRoutingHandler(commandController))
                routerBuilder.operation("requestDealCards").handler(RequestDealCardsRoutingHandler(commandController))
                routerBuilder.operation("pickUpCard").handler(PickUpCardRoutingHandler(commandController))
//                routerBuilder.operation("startGame").handler(StartGameRoutingHandler(controllerAdapter))

                //generate the router
                val router = routerBuilder.createRouter()
                //generate the 404 error handler
                router.errorHandler(404) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(404)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorResponse(404, routingContext.failure()?.message ?: "Not Found"))
                }
                //generate the 400 error handler
                router.errorHandler(400) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(400)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorResponse(400, routingContext.failure()?.message ?: "Validation Exception"))
                }

                //server creation
                vertx.createHttpServer(restHttpServiceConfig.getHttpServerOptions())
                    .requestHandler(router)
                    .listen()
                    .onFailure { ex ->
                        log.error("Rest API Server not started", ex)
                        startPromise?.fail(ex)
                    }
                    .onSuccess { arServer ->
                        server = arServer
                        startPromise?.complete()
                    }
            }
    }

    private fun errorResponse(httpCode: Int, message: String): String =
        JsonObject()
            .put("code", httpCode)
            .put("message", message)
            .encode()

    private fun extractAndLoadOpenAPISpec(fileName: String): String {
        val fs = vertx.fileSystem()

        // Try these approaches in order:
        // 1. Try loading as a resource and extract to temp file (JAR mode)
        val resourcePath = String.format("/%S", fileName)
        val resourceStream = javaClass.getResourceAsStream(resourcePath)
        var path: String = ""

        if (resourceStream != null) {
            println("Found OpenAPI spec as resource, extracting to temp file")
            try {
                // Create a temporary file that will be deleted on JVM exit
                val tempFile = File.createTempFile("openapi-spec", ".yaml")
                tempFile.deleteOnExit()

                // Read the resource and write to temp file using Vert.x
                val buffer = Buffer.buffer(resourceStream.readAllBytes())
                fs.writeFileBlocking(tempFile.absolutePath, buffer)

                println("Extracted OpenAPI spec to ${tempFile.absolutePath}")
                path = tempFile.absolutePath
            } catch (e: Exception) {
                throw RuntimeException("Failed to extract OpenAPI spec: ${e.message}", e)
            } finally {
                resourceStream.close()
            }
        } else {
            // 2. Try with alternative resource path
            val altResourcePath = fileName // Without leading slash
            val altResourceStream = javaClass.classLoader.getResourceAsStream(altResourcePath)

            if (altResourceStream != null) {
                println("Found OpenAPI spec as alternative resource, extracting to temp file")
                try {
                    val tempFile = File.createTempFile("openapi-spec", ".yaml")
                    tempFile.deleteOnExit()

                    val buffer = Buffer.buffer(altResourceStream.readAllBytes())
                    fs.writeFileBlocking(tempFile.absolutePath, buffer)

                    println("Extracted OpenAPI spec to ${tempFile.absolutePath}")
                    path = tempFile.absolutePath
                } catch (e: Exception) {
                    throw RuntimeException("Failed to extract OpenAPI spec: ${e.message}", e)
                } finally {
                    altResourceStream.close()
                }
            } else {
                throw RuntimeException("Could not find OpenAPI spec at any of the expected locations")
            }
        }

        return path
    }

}