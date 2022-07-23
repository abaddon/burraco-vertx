package com.abaddon83.burraco.common.models

import com.abaddon83.burraco.common.helpers.validUUID
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class PlayerIdentity constructor(
    @JsonProperty("identity")
    val identity: String
) : IIdentity {
    init {
        require(identity.validUUID()) { "Invalid UUID value received" }
    }

    override fun valueAsString(): String = identity

    companion object Factory {
        fun create(): PlayerIdentity = PlayerIdentity(UUID.randomUUID().toString())
    }

}



