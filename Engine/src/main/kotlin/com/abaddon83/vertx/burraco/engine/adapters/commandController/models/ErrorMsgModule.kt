package com.abaddon83.vertx.burraco.engine.adapters.commandController.models

data class ErrorMsgModule(
        val code: Int,
        val errorMessages: List<Any?>
)