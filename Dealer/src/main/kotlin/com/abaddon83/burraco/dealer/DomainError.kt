package com.abaddon83.burraco.dealer

import com.abaddon83.burraco.dealer.models.DealerIdentity

interface DomainError{
    abstract fun toMap():Map<String,*>
}

data class DealError(
    val message: String,
    val dealerIdentity: DealerIdentity?
): DomainError {
    //constructor(cmd: Command, exception: Exception, burracoGame: Game): this("Command ${cmd.javaClass.simpleName} not executed, exception type ${exception.javaClass.simpleName}",burracoGame.identity())
    //constructor(message: String, burracoGame: Game): this( message,burracoGame.identity())
    constructor(message: String): this( message,null)

    override fun toMap():Map<String,*> {
        val response = mapOf("message" to message)
        return when(dealerIdentity){
            is DealerIdentity -> response.plus("dealerIdentity" to dealerIdentity.valueAsString())
            else -> response
        }
    }
}

data class ExceptionError(
    val exception: Exception
): DomainError {
    override fun toMap():Map<String,*> =
        mapOf(
            "exception" to exception::class.simpleName,
            "message" to exception.message.orEmpty(),
            "stackTrace" to exception.stackTraceToString()
        )
}