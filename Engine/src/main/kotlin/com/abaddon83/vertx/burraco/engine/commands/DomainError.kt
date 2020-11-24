package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.vertx.burraco.engine.models.BurracoGame
import java.lang.Exception

sealed class DomainError(val msg: String){
    abstract fun toMap():Map<String,*>
}

data class BurracoGameError(
    val message: String,
    val burracoGameIdentity: GameIdentity?
): DomainError(message){
    constructor(cmd: Command,exception: Exception, burracoGame: BurracoGame): this("Command ${cmd.javaClass.simpleName} not executed, exception type ${exception.javaClass.simpleName}",burracoGame.identity())
    constructor(message: String, burracoGame: BurracoGame): this( message,burracoGame.identity())
    constructor(message: String): this( message,null)

    override fun toMap():Map<String,*> {
        val response = mapOf("message" to message)
        if(burracoGameIdentity != null) {
            return response.plus("gameIdentity" to burracoGameIdentity.convertTo().toString())
        }
        return response
    }
}