package com.abaddon83.vertx.burraco.game.adapters.commandController.handlers

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.vertx.burraco.game.adapters.commandController.models.ErrorMsgModule
import com.abaddon83.vertx.burraco.game.adapters.commandController.models.GameModule
import com.abaddon83.vertx.burraco.game.ports.CommandControllerPort
import com.abaddon83.vertx.burraco.game.ports.Outcome
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.RequestParameters
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.kotlin.core.json.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InitGameRoutingHandler(private val controllerAdapter: CommandControllerPort): RoutingHandler {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun handle(routingContext: RoutingContext) {
        val params: RequestParameters = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY)
        val bodyRequest = params.body()


        val gameIdentity = GameIdentity.create(params.pathParameter("gameId").string)
        checkNotNull(gameIdentity){"gameIdentity is null"}
        val playerIdentity = PlayerIdentity.create(bodyRequest.jsonObject.get<String>("playerId"))
        checkNotNull(playerIdentity){"playerIdentity is null"}

        when (val outcome: Outcome = controllerAdapter.initGame(gameIdentity,playerIdentity)) {
            is Valid -> routingContext
                .response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(200)
                .end(GameModule.from(outcome.value.game!!).toJson())
            is Invalid -> routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .setStatusCode(400)
                .end(Json.encodePrettily(ErrorMsgModule(code = 400, errorMessages = listOf(outcome.err.toMap()))))
        }
    }
}