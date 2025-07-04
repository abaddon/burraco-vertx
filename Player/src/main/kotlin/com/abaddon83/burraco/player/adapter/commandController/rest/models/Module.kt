package com.abaddon83.burraco.player.adapter.commandController.rest.models

import io.vertx.core.json.Json

abstract class Module {
    fun toJson(): String{
        return Json.encode(this);
    }
}