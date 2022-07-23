package com.abaddon83.burraco.dealer.models

import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class DealerIdentity private constructor(
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {
//
        fun empty(): DealerIdentity = DealerIdentity("00000000-0000-0000-0000-000000000000")
        fun create(): DealerIdentity = DealerIdentity(UUID.randomUUID().toString())
//
//        fun create(uuidString: String): PlayerIdentity {
//            require(uuidString.validUUID()) { "Invalid UUID value received" }
//            return PlayerIdentity(uuidString)
//        }
    }

}



