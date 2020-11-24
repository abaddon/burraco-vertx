package com.abaddon83.burraco.common.models.identities

import com.abaddon83.utils.ddd.identity.UUIDIdentity
import com.abaddon83.utils.serializations.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class BurracoIdentity private constructor(
    @Serializable(UUIDSerializer::class)
    override val id: UUID
) : UUIDIdentity() {

    constructor(): this(UUIDIdentity.emptyValue)

    companion object Factory {
        fun create(): BurracoIdentity = BurracoIdentity(UUID.randomUUID())
        fun create(uuidString: String): BurracoIdentity {
            val uuid = UUID.fromString(uuidString)
            return BurracoIdentity(uuid)
        }
    }
}

