package com.abaddon83.burraco.common.models

import com.abaddon83.burraco.common.helpers.validUUID
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.abaddon.kcqrs.core.IIdentity
import java.util.*

data class GameIdentity constructor(
    @JsonProperty("identity")
    val identity: String
) : IIdentity {
    init {
        require(identity.validUUID()) { "Invalid UUID value received" }
    }

    override fun valueAsString(): String = identity

    companion object Factory {

        fun create(): GameIdentity = GameIdentity(UUID.randomUUID().toString())

        fun empty(): GameIdentity = GameIdentity("00000000-0000-0000-0000-000000000000")
    }

}



