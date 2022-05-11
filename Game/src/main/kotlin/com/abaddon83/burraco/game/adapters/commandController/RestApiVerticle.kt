package com.abaddon83.burraco.game.adapters.commandController

import com.abaddon83.burraco.game.HealthCheck
import com.abaddon83.burraco.game.adapters.commandController.config.RestApiConfig
import com.abaddon83.burraco.game.adapters.commandController.handlers.*
import com.abaddon83.burraco.game.ports.CommandControllerPort
import com.abaddon83.burraco.game.vertx.AbstractHttpServiceVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.openapi.Operation
import io.vertx.ext.web.openapi.RouterBuilder
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class RestApiVerticle(
    private val restApiConfig: RestApiConfig,
    private val controllerAdapter: CommandControllerPort
    ) : AbstractHttpServiceVerticle() {

    private val log = LoggerFactory.getLogger(this::class.qualifiedName)

//    private val controllerAdapter: CommandControllerPort
//        get() = CommandControllerAdapter(vertx)

    override fun start(startFuture: Promise<Void>?) {
        startHttpServer(startFuture);
    }

//    override fun stop(endPromise: Promise<Void>?) {
// //        stop()
//        endPromise?.complete()
//    }

    private fun startHttpServer(startPromise: Promise<Void>?) {
        val apiDefinitionUrl = this.javaClass.classLoader.getResource("gameAPIs.yaml")
        val healthCheck = HealthCheck(vertx)
        RouterBuilder.create(vertx, apiDefinitionUrl.toString())
            .onSuccess { routerBuilder ->
                routerBuilder.operation("healthCheck").handler(healthCheck.build())
                routerBuilder.operation("newGame").handler(NewGameRoutingHandler(controllerAdapter))
//                routerBuilder.operation("addPlayer").handler(AddPlayerRoutingHandler(controllerAdapter))
//                routerBuilder.operation("initGame").handler(InitGameRoutingHandler(controllerAdapter))
//                routerBuilder.operation("startGame").handler(StartGameRoutingHandler(controllerAdapter))

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
                vertx.createHttpServer(restApiConfig.getHttpServerOptions())
                    .requestHandler(router)
                    .listen()
                    .onSuccess { arServer ->
                        server = arServer
                        startPromise?.complete()
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
                    //.onFailure(startPromise::fail)
            }
            //.onFailure(startPromise::fail)
    }
}

//private fun Operation.coroutineHandler(fn: suspend (RoutingContext) -> Unit) = handler {
//    GlobalScope.launch(it.vertx().dispatcher()) {
//        try {
//            fn(it)
//        } catch (e: Exception) {
//            it.fail(e)
//        }
//    }
//}