package com.abaddon83.burraco.game

sealed class DomainError(val msg: String){
    abstract fun toMap():Map<String,*>
}

//data class GameError(
//    val message: String,
//    val gameIdentity: GameIdentity?
//): DomainError(message){
//    constructor(cmd: Command, exception: Exception, burracoGame: Game): this("Command ${cmd.javaClass.simpleName} not executed, exception type ${exception.javaClass.simpleName}",burracoGame.identity())
//    constructor(message: String, burracoGame: Game): this( message,burracoGame.identity())
//    constructor(message: String): this( message,null)
//
//    override fun toMap():Map<String,*> {
//        val response = mapOf("message" to message)
//        if(gameIdentity != null) {
//            return response.plus("gameIdentity" to gameIdentity.convertTo().toString())
//        }
//        return response
//    }
//}