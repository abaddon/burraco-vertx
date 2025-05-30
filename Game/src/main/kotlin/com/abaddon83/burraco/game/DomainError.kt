package com.abaddon83.burraco.game

import com.abaddon83.burraco.common.models.GameIdentity


interface DomainError{
    abstract fun toMap():Map<String,*>
}

data class GameError(
    val message: String,
    val gameIdentity: GameIdentity?
): DomainError {
    //constructor(cmd: Command, exception: Exception, burracoGame: Game): this("Command ${cmd.javaClass.simpleName} not executed, exception type ${exception.javaClass.simpleName}",burracoGame.identity())
    //constructor(message: String, burracoGame: Game): this( message,burracoGame.identity())
    constructor(message: String): this( message,null)

    override fun toMap():Map<String,*> {
        val response = mapOf("message" to message)
        return when(gameIdentity){
            is GameIdentity -> response.plus("gameIdentity" to gameIdentity.valueAsString())
            else -> response
        }
    }
}

data class ExceptionError(
    val exception: Throwable
): DomainError{
    override fun toMap():Map<String,*> =
        mapOf(
            "exception" to exception::class.simpleName,
            "message" to exception.message.orEmpty()//,
            //"stackTrace" to exception.stackTraceToString()
        )
}