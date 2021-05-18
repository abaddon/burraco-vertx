package com.abaddon83.vertx.burraco.game.adapters.commandController

import com.abaddon83.vertx.burraco.game.adapters.eventStoreAdapter.vertx.EventStoreVertxAdapter
import com.abaddon83.vertx.burraco.game.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.game.ports.EventStorePort
import io.vertx.core.Promise
import io.vertx.core.Vertx


class CommandControllerRoutes(private val vertx: Vertx) {

    val apiDefinitionUrl = this.javaClass.classLoader.getResource("gameApi.yaml")

    private val eventStore: EventStorePort
        get() = EventStoreVertxAdapter(vertx)

    private val controllerAdapter: CommandControllerPort
        get() = CommandControllerAdapter(vertx)

    fun getRouters(startPromise: Promise<Void>) {
//        RouterBuilder.create(vertx,apiDefinitionUrl.toString())
//            .onSuccess{ routerBuilder ->
//                routerBuilder.operation("newGame").handler(NewGameRoutingHandler(controllerAdapter))
//                routerBuilder.operation("addPlayer").handler(AddPlayerRoutingHandler(controllerAdapter))
//                routerBuilder.operation("startGame").handler(StartGameRoutingHandler(controllerAdapter))
//
//                //generate the router
//                val router = routerBuilder.createRouter()
//
//                //generate the 404 error handler
//                router.errorHandler(404) { routingContext ->
//                    routingContext
//                        .response()
//                        .setStatusCode(404)
//                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                        .end(
//                            //ErrorSchema(404,routingContext.failure()?.message ?: "Not Found").toJson()
//                        )
//                }
//                //generate the 400 error handler
//                router.errorHandler(400) { routingContext ->
//                    routingContext
//                        .response()
//                        .setStatusCode(400)
//                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                        .end(
//                            //ErrorSchema(400,routingContext.failure()?.message ?: "Validation Exception").toJson()
//                        )
//                }
//
//                //server creation
//                val serverOptions = HttpServerOptions().setPort(config.port)
//                config.address ?.let { serverOptions.setHost(it) }
//                server = vertx.createHttpServer(serverOptions)
//                server.requestHandler(router).listen()
//                startPromise.complete()
//            }
//            .onFailure(startPromise::fail)

//        val router: Router = Router.router(vertx)
//        router.route().handler(BodyHandler.create())
//
//        router.post("/games/burraco").handler(CreateGameRequest.getValidator()).handler(::createNewBurracoGameHandler)
//            .failureHandler(::failureHandler)
//        router.post("/games/burraco/:gameId/join").handler(JoinGameRequest.getValidator()).handler(::joinPlayerHandler)
//            .failureHandler(::failureHandler)
//        router.post("/games/burraco/:gameId/start").handler(StartGameRequest.getValidator())
//            .handler(::startPlayerHandler)
//            .failureHandler(::failureHandler)

    }

//    private fun createNewBurracoGameHandler(routingContext: RoutingContext) {
//        try {
//            when (val outcome: Outcome = controllerAdapter.createNewBurracoGame(GameIdentity.create())) {
//                is Valid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(200)
//                    .end(Json.encodePrettily(outcome))
//                is Invalid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(400)
//                    .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(outcome.err.toMap()))))
//            }
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//
//    private fun joinPlayerHandler(routingContext: RoutingContext) {
//        val requestJson = routingContext.bodyAsJson
//        val gameIdentity = GameIdentity.create(routingContext.request().getParam("gameId"))!!
//        val playerIdentity = PlayerIdentity.create(requestJson.getString("playerIdentity"))!!
//        updateLocalEventsCache(gameIdentity) {
//            when (val outcome = controllerAdapter.addPlayer(gameIdentity, playerIdentity = playerIdentity)) {
//                is Valid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(200)
//                    .end(Json.encodePrettily(outcome))
//                is Invalid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(400)
//                    .end(
//                        Json.encodePrettily(
//                            ErrorMsgModule(
//                                code = 400,
//                                errorMessages = listOf(outcome.err.toMap())
//                            )
//                        )
//                    )
//            }
//        }
//    }

//    private fun startPlayerHandler(routingContext: RoutingContext) {
//        val requestJson = routingContext.bodyAsJson
//        val gameIdentity = GameIdentity.create(routingContext.request().getParam("gameId"))!!
//        val playerIdentity = PlayerIdentity.create(requestJson.getString("playerIdentity"))!!
//        updateLocalEventsCache(gameIdentity) {
//            when (val outcome = controllerAdapter.startGame(gameIdentity, playerIdentity = playerIdentity)) {
//                is Valid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(200)
//                    .end(Json.encodePrettily(outcome))
//                is Invalid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(400)
//                    .end(
//                        Json.encodePrettily(
//                            ErrorMsgModule(
//                                code = 400,
//                                errorMessages = listOf(outcome.err.toMap())
//                            )
//                        )
//                    )
//            }
//        }
//    }


//    private fun updateLocalEventsCache(
//        aggregateIdentity: GameIdentity,
//        block: (AsyncResult<List<ExtendEvent>>) -> Unit
//    ) {
//        val eventStoreVertxAdapter = (eventStore as EventStoreVertxAdapter)
//        eventStoreVertxAdapter
//            //retrieve the events from the EventStore
//            .getEvents(aggregateIdentity.convertTo().toString(), "BurracoGame")
//            .future()
//            .onComplete { ar ->
//                //load events in a local cache.. workaround to adapt the current model to Vertx..
//                eventStoreVertxAdapter
//                    .loadExtendedEvents(aggregateIdentity.convertTo().toString(), ar.result())
//                block(ar)
//            }.onFailure {
//                throw it
//            }
//    }

//    private fun failureHandler(routingContext: RoutingContext) {
//        val errorMsgModule = when (val failure = routingContext.failure()) {
//            is ValidationException -> ErrorMsgModule(
//                code = 400, errorMessages = listOf(
//                    mapOf(
//                        ("message" to failure.message),
//                        ("cause" to failure.cause),
//                        ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
//                    )
//                )
//            )
//            else -> ErrorMsgModule(
//                code = 500, errorMessages = listOf(
//                    mapOf(
//                        ("message" to failure.message),
//                        ("cause" to failure.cause),
//                        ("stacktrace" to failure.stackTrace.map { st -> "${st.fileName} ${st.methodName} (${st.lineNumber})" })
//                    )
//                )
//            )
//        }
//
//        routingContext.response()
//            .putHeader("content-type", "application/json; charset=utf-8")
//            .setStatusCode(errorMsgModule.code)
//            .end(Json.encodePrettily(errorMsgModule))
//    }

}