package com.abaddon83.vertx.burraco.game.adapters.commandController

import com.abaddon83.utils.vertx.AbstractHttpServiceVerticle
import com.abaddon83.vertx.burraco.game.adapters.commandController.config.HttpConfig
import com.abaddon83.vertx.burraco.game.adapters.commandController.handlers.AddPlayerRoutingHandler
import com.abaddon83.vertx.burraco.game.adapters.commandController.handlers.NewGameRoutingHandler
import com.abaddon83.vertx.burraco.game.adapters.commandController.handlers.StartGameRoutingHandler
import com.abaddon83.vertx.burraco.game.ports.CommandControllerPort
import io.vertx.core.Promise
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.openapi.RouterBuilder
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class RestApiVerticle(val httpConfig: HttpConfig, private val controllerAdapter: CommandControllerPort) :
    AbstractHttpServiceVerticle() {

    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

//    private val controllerAdapter: CommandControllerPort
//        get() = CommandControllerAdapter(vertx)

    override fun start(startPromise: Promise<Void>) {
        startHttpServer(startPromise);
    }

    fun startHttpServer(startPromise: Promise<Void>) {
        val apiDefinitionUrl = this.javaClass.classLoader.getResource("gameAPIs.yaml")
        //val commandControllerRoutes = CommandControllerRoutes(vertx)
        RouterBuilder.create(vertx, apiDefinitionUrl.toString())
            .onSuccess { routerBuilder ->
                routerBuilder.operation("newGame").handler(NewGameRoutingHandler(controllerAdapter))
                routerBuilder.operation("addPlayer").handler(AddPlayerRoutingHandler(controllerAdapter))
                routerBuilder.operation("startGame").handler(StartGameRoutingHandler(controllerAdapter))

                //generate the router
                val router = routerBuilder.createRouter()

                //generate the 404 error handler
                router.errorHandler(404) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(404)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(
                            //ErrorSchema(404,routingContext.failure()?.message ?: "Not Found").toJson()
                        )
                }
                //generate the 400 error handler
                router.errorHandler(400) { routingContext ->
                    routingContext
                        .response()
                        .setStatusCode(400)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(
                            //ErrorSchema(400,routingContext.failure()?.message ?: "Validation Exception").toJson()
                        )
                }

                //server creation
                vertx.createHttpServer(httpConfig.getHttpServerOptions())
                    .requestHandler(router)
                    .listen()
                    .onSuccess { arServer ->
                        server = arServer
                        startPromise.complete()
//                        launch(vertx.dispatcher()) {
//                            publishServiceRecord(httpConfig.serviceName,httpConfig.host,httpConfig.port,httpConfig.root).future()
//                                .onComplete {
//                                    discovery.close()
//                                    startPromise.complete()
//                                }
//                                .onFailure {
//                                    discovery.close()
//                                    startPromise.fail(it)
//                                }
//                        }
                    }
                    .onFailure(startPromise::fail)
            }
            .onFailure(startPromise::fail)
    }
}