package com.abaddon83.burraco.common.models.identities

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class PlayerIdentityTest{

    @Test
    fun `Given nothing when I create a new PlayerIdentity then I'll have a new PlayerEntity with a random key`(){
        assertNotNull(PlayerIdentity.create())
    }

    @Test
    fun `Given a UUID, when I create a new PlayerIdentity using it then I have a playerIdentity with the same UUID key`(){
        val expectedUUID = UUID.randomUUID()
        val identity = PlayerIdentity(expectedUUID)
        assert(identity.convertTo() == expectedUUID)
    }

    @Test
    fun `Given a UUID string, when I create a new PlayerIdentity using it then I have a playerIdentity with the same UUID key`(){
        val expectedUUIDString = UUID.randomUUID().toString()
        val identity = PlayerIdentity.create(expectedUUIDString)!!
        assert(identity.convertTo().toString() == expectedUUIDString)
    }
    @Test
    fun `Given a not valid UUID string, when I create a new PlayerIdentity using it then I receive a null PlayerIdentity`(){
        assertNull(PlayerIdentity.create("fake-UUID"), "the PlayerIdentity should be null")
    }

    @Test
    fun `Given a PlayerIdentity when I serialise and deserialize it, then I should have the same PlayerIdentity deserialized`() {

        val expectedIdentity = PlayerIdentity.create(UUID.randomUUID().toString())!!

        val jsonString = Json.encodeToString(expectedIdentity);
        val deserializedIdentity = Json.decodeFromString<PlayerIdentity>(jsonString)

        assertEquals(expectedIdentity,deserializedIdentity)

    }
}