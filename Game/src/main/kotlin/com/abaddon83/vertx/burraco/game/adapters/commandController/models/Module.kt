package com.abaddon83.vertx.burraco.game.adapters.commandController.models

import io.vertx.core.json.Json

abstract class Module {
    fun toJson(): String{
        return Json.encode(this);
        //return Json.encode(this)
        //return Json.encodePrettily(this)
    }
}