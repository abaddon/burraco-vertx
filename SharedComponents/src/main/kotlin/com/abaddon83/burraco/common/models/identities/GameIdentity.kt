package com.abaddon83.burraco.common.models.identities

import com.abaddon83.burraco.common.serializations.UUIDCustomSerializer
import com.abaddon83.utils.ddd.identity.UUIDIdentity
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class GameIdentity(
    @Serializable(UUIDCustomSerializer::class)
    override val id: UUID
) : UUIDIdentity() {

    //TODO I don't like it
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



