package com.abaddon83.burraco.player

import com.abaddon83.burraco.common.models.PlayerIdentity

sealed class DomainError(override val message: String) : Exception(message) {

    abstract fun toMap(): Map<String, *>

    data class PlayerError(
        override val message: String,
        val playerIdentity: PlayerIdentity?
    ) : DomainError(message) {
        override fun toMap(): Map<String, *> {
            val response = mapOf("message" to message)
            return when (playerIdentity) {
                is PlayerIdentity -> response.plus("playerIdentity" to playerIdentity.valueAsString())
                else -> response
            }
        }
    }

    data class ExceptionError(val exception: Throwable) :
        DomainError(exception.message.orEmpty()) {
        override fun toMap(): Map<String, *> =
            mapOf(
                "exception" to exception::class.simpleName,
                "message" to exception.message.orEmpty()
            )
    }
}