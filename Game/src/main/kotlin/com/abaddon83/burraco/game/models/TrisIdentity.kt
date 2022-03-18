package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.helpers.ValidationsTools.validUUID
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class TrisIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): TrisIdentity = TrisIdentity(UUID.randomUUID().toString())

        fun create(uuidString: String): TrisIdentity {
            require(validUUID(uuidString)) { "Invalid UUID value received" }
            return TrisIdentity(uuidString)
        }
    }

}



