package com.abaddon83.burraco.common.models.identities

import com.abaddon83.utils.ddd.identity.UUIDIdentity
import com.abaddon83.utils.serializations.UUIDSerializer
import kotlinx.serialization.Serializable
import java.lang.Exception
import java.util.*

@Serializable
data class GameIdentity(
    @Serializable(UUIDSerializer::class)
    override val id: UUID
) : UUIDIdentity() {

    constructor(): this(UUIDIdentity.emptyValue)

    companion object Factory {
        fun create(): GameIdentity = GameIdentity(UUID.randomUUID())
        fun create(uuidString: String): GameIdentity? {
            return try{
                val uuid = UUID.fromString(uuidString)
                GameIdentity(uuid)
            } catch (ex: Exception){
                null
            }

        }
    }

}



