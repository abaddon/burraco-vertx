package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.vertx.burraco.engine.models.BurracoGame
import java.lang.Exception

sealed class DomainError(val msg: String)

data class BurracoGameError(val e: String, val burracoGame: BurracoGame): DomainError(e){
    constructor(cmd: Command,exception: Exception, burracoGame: BurracoGame): this("Command ${cmd.javaClass.simpleName} not executed, exception type ${exception.javaClass.simpleName}",burracoGame)
}