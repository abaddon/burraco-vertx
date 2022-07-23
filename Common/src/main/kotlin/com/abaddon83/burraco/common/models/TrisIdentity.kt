package com.abaddon83.burraco.common.models

import com.abaddon83.burraco.common.helpers.validUUID
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class TrisIdentity private constructor(
    @JsonProperty("identity")
    val identity: String
) : IIdentity {

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): TrisIdentity = TrisIdentity(UUID.randomUUID().toString())

        fun create(uuidString: String): TrisIdentity {
            require(uuidString.validUUID()) { "Invalid UUID value received" }
            return TrisIdentity(uuidString)
        }
    }

}



