package com.abaddon83.burraco.game.adapters.commandController.rest.models

import io.vertx.core.json.Json

abstract class Module {
    fun toJson(): String{
        return Json.encode(this);
        //return Json.encode(this)
        //return Json.encodePrettily(this)
    }
}