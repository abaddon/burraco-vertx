package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.helpers.ValidationsTools.isValidUUID
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class GameIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): GameIdentity = GameIdentity(UUID.randomUUID().toString())

        fun create(uuidString: String): GameIdentity {
            require(isValidUUID(uuidString)) { "Invalid UUID value received" }
            return GameIdentity(uuidString)
        }
    }

}



