package com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests

import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.ext.web.api.validation.ParameterType
import io.vertx.ext.web.api.validation.ParameterTypeValidator

data class CreateGameRequest(val gameType: GameType) {

    companion object {
        fun getValidator(): HTTPRequestValidationHandler = HTTPRequestValidationHandler.create()
            .addJsonBodySchema("{\n" +
                    "    \"required\": [\n" +
                    "        \"gameType\"\n" +
                    "    ],\n" +
                    "    \"properties\": {\n" +
                    "        \"gameType\": {\n" +
                    "            \"enum\": [\n" +
                    "                \"BURRACO\"\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"additionalProperties\": false\n" +
                    "}")
    }
}

enum class GameType{ BURRACO }