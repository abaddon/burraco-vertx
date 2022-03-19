package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.game.helpers.validUUID
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class PlayerIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): PlayerIdentity = PlayerIdentity(UUID.randomUUID().toString())

        fun create(uuidString: String): PlayerIdentity {
            require(uuidString.validUUID()) { "Invalid UUID value received" }
            return PlayerIdentity(uuidString)
        }
    }

}



