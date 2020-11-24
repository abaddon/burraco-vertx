package com.abaddon83.vertx.burraco.engine.adapters.commandController.bodyRequests

import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler
import io.vertx.ext.web.api.validation.ParameterType
import java.util.*


data class StartGameRequest(
        val playerIdentity: UUID
){
        companion object {
                fun getValidator(): HTTPRequestValidationHandler = HTTPRequestValidationHandler.create()
                        .addPathParam("gameId", ParameterType.UUID)
                        .addJsonBodySchema("{\n" +
                                "    \"required\": [\n" +
                                "        \"playerIdentity\"\n" +
                                "    ],\n" +
                                "    \"properties\": {\n" +
                                "        \"playerIdentity\": {\n" +
                                "            \"type\": \"string\"\n," +
                                "            \"format\": \"uuid\"\n" +
                                "        }\n" +
                                "    },\n" +
                                "    \"additionalProperties\": false\n" +
                                "}")
        }
}
