package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.helpers.validUUID
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class StraightIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): StraightIdentity = StraightIdentity(UUID.randomUUID().toString())

        fun create(uuidString: String): StraightIdentity {
            require(uuidString.validUUID()) { "Invalid UUID value received" }
            return StraightIdentity(uuidString)
        }
    }

}



