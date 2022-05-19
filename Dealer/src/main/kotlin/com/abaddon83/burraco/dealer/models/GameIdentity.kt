package com.abaddon83.burraco.dealer.models

import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class GameIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {
//
        fun create(): GameIdentity = GameIdentity(UUID.randomUUID().toString())
//
//        fun create(uuidString: String): GameIdentity {
//            require(uuidString.validUUID()) { "Invalid UUID value received" }
//            return GameIdentity(uuidString)
//        }
//
        fun empty(): GameIdentity = GameIdentity("00000000-0000-0000-0000-000000000000")
    }

}



