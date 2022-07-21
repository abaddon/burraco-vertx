package com.abaddon83.burraco.game.adapters.commandController.rest

import com.abaddon83.burraco.common.vertx.AbstractHttpServiceVerticle
import com.abaddon83.burraco.game.HealthCheck
import com.abaddon83.burraco.game.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.game.adapters.commandController.rest.config.RestHttpServiceConfig
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.AddPlayerRoutingHandler
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.NewGameRoutingHandler
import com.abaddon83.burraco.game.adapters.commandController.rest.handlers.RequestDealCardsRoutingHandler
import com.abaddon83.burraco.game.commands.AggregateGameCommandHandler
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.ports.CommandControllerPort
import com.abaddon83.burraco.game.ports.GameEventPublisherPort
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository
import io.vertx.core.Promise
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.openapi.RouterBuilder
import org.slf4j.LoggerFactory


class RestHttpServiceVerticle(
    private val restHttpServiceConfig: RestHttpServiceConfig,
    private val controllerAdapter: CommandControllerPort
) : AbstractHttpServiceVerticle() {
    private val log = LoggerFactory.getLogger(this::class.qualifiedName)


    override fun start(startFuture: Promise<Void>?) {
        startHttpServer(startFuture);
    }

    private fun startHttpServer(startPromise: Promise<Void>?) {
        val healthCheck = HealthCheck(vertx)
        RouterBuilder.create(vertx, restHttpServiceConfig.openApiPath)
            .onFailure { ex ->
                log.error("OpenApi not loaded", ex)
                startPromise?.fail(ex)
            }
            .onSuccess { routerBuilder ->
                routerBuilder.operation("healthCheck").handler(healthCheck.build())
                routerBuilder.operation("newGame").handler(NewGameRoutingHandler(controllerAdapter))
                routerBuilder.operation("addPlayer").handler(AddPlayerRoutingHandler(controllerAdapter))
                routerBuilder.operation("requestDealCards").handler(RequestDealCardsRoutingHandler(controllerAdapter))
//                routerBuilder.operation("startGame").handler(StartGameRoutingHandler(controllerAdapter))

                //generate the router
                val router = routerBuilder.createRouter()
                //generate the 404 error handler
                router.errorHandler(404) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(404)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorResponse(400, routingContext.failure().message ?: "Not Found"))
                }
                //generate the 400 error handler
                router.errorHandler(400) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(400)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(errorResponse(400, routingContext.failure().message ?: "Validation Exception"))
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

    companion object {
        fun build(
            restHttpServiceConfig: RestHttpServiceConfig,
            repository: IAggregateRepository<Game>,
            gameEventPublisher: GameEventPublisherPort
        ): RestHttpServiceVerticle {
            val commandControllerAdapter =
                CommandControllerAdapter(AggregateGameCommandHandler(repository, gameEventPublisher))
            return RestHttpServiceVerticle(restHttpServiceConfig, commandControllerAdapter)
        }
    }

    //    override fun stop(endPromise: Promise<Void>?) {
// //        stop()
//        endPromise?.complete()
//    }
}