package com.abaddon83.burraco.common.models.identities


import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameIdentityTest {
    @Test
    fun `new GameIdentity using UUID`(){
        val expectedUUID = UUID.randomUUID()
        val identity = GameIdentity(expectedUUID)
        assert(identity.convertTo() == expectedUUID)
    }
    @Test
    fun `new GameIdentity using a valid UUID String`(){
        val expectedUUID = UUID.randomUUID()
        val identity = GameIdentity.create(expectedUUID.toString())!!
        assert(identity.convertTo() == expectedUUID)
    }
    @Test
    fun `new GameIdentity using a not valid UUID String should fail`(){

        assertNull(GameIdentity.create("fake-UUID"),"the GameIdentity should be null")

        }

    @Test
    fun `given a GameIdentity when I serialise it, then I should have the same GameIdentity deserialized`() {
        val expectedUUID = UUID.randomUUID()
        val identity = GameIdentity.create(expectedUUID.toString())!!

        val jsonString = Json.encodeToString(identity);
        val deserializedTris = Json.decodeFromString<GameIdentity>(jsonString)


//        val jsonString = Json.encodeToString(identity);
//        val deserializedTris = Json.decodeFromString<GameIdentity>(jsonString)
        assertEquals(identity.id,deserializedTris.id)

    }
}