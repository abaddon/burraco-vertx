package com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests

import java.util.*

//@JsonDeserialize(using = JoinGameRequestDeserializer::class)
data class JoinGameRequest(
        val playerIdentity: UUID
)

