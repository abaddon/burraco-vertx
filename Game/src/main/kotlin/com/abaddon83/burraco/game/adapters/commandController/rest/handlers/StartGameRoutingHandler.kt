//package com.abaddon83.burraco.game.adapters.commandController.handlers
//
//import com.abaddon83.burraco.common.models.identities.GameIdentity
//import com.abaddon83.utils.functionals.Invalid
//import com.abaddon83.utils.functionals.Valid
//import com.abaddon83.burraco.game.adapters.commandController.models.ErrorMsgModule
//import com.abaddon83.burraco.game.adapters.commandController.models.GameModule
//import com.abaddon83.burraco.game.ports.CommandControllerPort
//import com.abaddon83.burraco.game.ports.Outcome
//import io.vertx.core.http.HttpHeaders
//import io.vertx.core.json.Json
//import io.vertx.ext.web.RoutingContext
//import io.vertx.ext.web.validation.RequestParameters
//import io.vertx.ext.web.validation.ValidationHandler
//
//class StartGameRoutingHandler(private val controllerAdapter: CommandControllerPort): RoutingHandler {
//
//    override fun handle(routingContext: RoutingContext) {
//        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
//
//        val gameIdentity = GameIdentity.create(params.pathParameter("gameId").string)
//        checkNotNull(gameIdentity){"gameIdentity is null"}
//        controllerAdapter.startGame(gameIdentity).future().onSuccess { outcome ->
//            when (outcome) {
//                is Valid -> routingContext
//                    .response()
//                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                    .setStatusCode(200)
//                    .end(GameModule.from(outcome.value.game!!).toJson())
//                is Invalid -> routingContext.response()
//                    .putHeader("content-type", "application/json; charset=utf-8")
//                    .setStatusCode(400)
//                    .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(outcome.err.toMap()))))
//            }
//        }
//
//    }
//}