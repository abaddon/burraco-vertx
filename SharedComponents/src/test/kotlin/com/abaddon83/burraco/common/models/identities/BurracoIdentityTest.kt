package com.abaddon83.burraco.common.models.identities

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class BurracoIdentityTest{
    @Test
    fun `Given nothing when I create a new BurracoIdentity then I'll have a new PlayerEntity with a random key`(){
        assertNotNull(BurracoIdentity.create())
    }

//    @Test
//    fun `Given a UUID, when I create a new BurracoIdentity using it then I have a BurracoIdentity with the same UUID key`(){
//        val expectedUUID = UUID.randomUUID()
//        val identity = BurracoIdentity(expectedUUID)
//        assert(identity.convertTo() == expectedUUID)
//    }

    @Test
    fun `Given a UUID string, when I create a new BurracoIdentity using it then I have a BurracoIdentity with the same UUID key`(){
        val expectedUUIDString = UUID.randomUUID().toString()
        val identity = BurracoIdentity.create(expectedUUIDString)!!
        assert(identity.convertTo().toString() == expectedUUIDString)
    }
    @Test
    fun `Given a not valid UUID string, when I create a new BurracoIdentity using it then I receive a null BurracoIdentity`(){
        assertThrows(IllegalArgumentException::class.java){
            BurracoIdentity.create("fake-UUID")
        }
    }

    @Test
    fun `Given a BurracoIdentity when I serialise and deserialize it, then I should have the same BurracoIdentity deserialized`() {

        val expectedIdentity = BurracoIdentity.create(UUID.randomUUID().toString())!!

        val jsonString = Json.encodeToString(expectedIdentity);
        val deserializedIdentity = Json.decodeFromString<BurracoIdentity>(jsonString)

        assertEquals(expectedIdentity,deserializedIdentity)

    }
}