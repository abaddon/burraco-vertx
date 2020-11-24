package com.abaddon83.burraco.common.models.identities

import com.abaddon83.utils.ddd.identity.UUIDIdentity
import com.abaddon83.utils.serializations.UUIDSerializer
import kotlinx.serialization.Serializable
import java.lang.Exception
import java.util.*

@Serializable
data class PlayerIdentity constructor(
    @Serializable(UUIDSerializer::class)
    override val id: UUID
) : UUIDIdentity() {

    constructor(): this(UUIDIdentity.emptyValue)

    companion object Factory {
        fun create(): PlayerIdentity = PlayerIdentity(UUID.randomUUID())
        fun create(uuidString: String): PlayerIdentity? {
            return try{
                val uuid = UUID.fromString(uuidString)
                PlayerIdentity(uuid)
            }catch (ex: Exception){
                null
            }

        }
    }

}



