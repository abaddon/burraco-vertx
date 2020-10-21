package com.abaddon83.vertx.burraco.engine.models.players

import com.abaddon83.utils.ddd.UUIDIdentity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.Exception
import java.util.*

@Serializable(with = PlayerIdentityCustomSerializer::class)
data class PlayerIdentity constructor(val id: UUID) : UUIDIdentity(id) {

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

object PlayerIdentityCustomSerializer: KSerializer<PlayerIdentity> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PlayerIdentity", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PlayerIdentity) {
        val string = value.convertTo().toString()
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): PlayerIdentity {
        val uuidString = decoder.decodeString()
        return PlayerIdentity.create(uuidString)!!
    }
}