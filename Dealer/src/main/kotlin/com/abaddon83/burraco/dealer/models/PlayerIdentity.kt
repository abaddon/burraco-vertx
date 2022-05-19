package com.abaddon83.burraco.dealer.models

import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class PlayerIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {
//
        fun create(): PlayerIdentity = PlayerIdentity(UUID.randomUUID().toString())
//
//        fun create(uuidString: String): PlayerIdentity {
//            require(uuidString.validUUID()) { "Invalid UUID value received" }
//            return PlayerIdentity(uuidString)
//        }
    }

}



