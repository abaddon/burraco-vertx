package com.abaddon83.burraco.common.models.identities

import com.abaddon83.burraco.common.serializations.UUIDCustomSerializer
import com.abaddon83.utils.ddd.identity.UUIDIdentity
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class BurracoIdentity private constructor(
    @Serializable(UUIDCustomSerializer::class)
    override val id: UUID
) : UUIDIdentity() {

    //TODO I don't like it
    constructor(): this(UUIDIdentity.emptyValue)

    companion object Factory {
        fun create(): BurracoIdentity = BurracoIdentity(UUID.randomUUID())
        fun create(uuidString: String): BurracoIdentity {
            val uuid = UUID.fromString(uuidString)
            return BurracoIdentity(uuid)
        }
    }
}

