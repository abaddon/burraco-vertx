package com.abaddon83.burraco.common.models.identities


import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class GameIdentityTest {

    @Test
    fun `Given nothing when I create a new GameIdentity then I'll have a new GameIdentity with a random key`(){
        Assertions.assertNotNull(GameIdentity.create())
    }

    @Test
    fun `Given a UUID, when I create a new GameIdentity using it then I have a GameIdentity with the same UUID key`(){
        val expectedUUID = UUID.randomUUID()
        val identity = GameIdentity(expectedUUID)
        assert(identity.convertTo() == expectedUUID)
    }

    @Test
    fun `Given a UUID string, when I create a new GameIdentity using it then I have a GameIdentity with the same UUID key`(){
        val expectedUUIDString = UUID.randomUUID().toString()
        val identity = GameIdentity.create(expectedUUIDString)!!
        assert(identity.convertTo().toString() == expectedUUIDString)
    }
    @Test
    fun `Given a not valid UUID string, when I create a new GameIdentity using it then I receive a null GameIdentity`(){
        Assertions.assertNull(GameIdentity.create("fake-UUID"), "the GameIdentity should be null")
    }

    @Test
    fun `Given a GameIdentity when I serialise and deserialize it, then I should have the same GameIdentity deserialized`() {

        val expectedIdentity = GameIdentity.create(UUID.randomUUID().toString())!!

        val jsonString = Json.encodeToString(expectedIdentity);
        val deserializedIdentity = Json.decodeFromString<GameIdentity>(jsonString)

        Assertions.assertEquals(expectedIdentity, deserializedIdentity)

    }
}