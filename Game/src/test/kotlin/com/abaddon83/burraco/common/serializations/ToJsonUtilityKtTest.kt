package com.abaddon83.burraco.common.serializations

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class ToJsonUtilityKtTest{

    @Test
    fun testToJsonWithEmptyList(){
        val playerlist = listOf<PlayerIdentity>()
        val jsonString = Json.encodeToString(playerlist)
        assertEquals("[]",jsonString)

    }

    @Test
    fun testToJsonWithPlayerIdentityList(){
        val playerlist = listOf<PlayerIdentity>(PlayerIdentity.create(),PlayerIdentity.create())
        val jsonString = Json.encodeToString(playerlist)
        println("jsonString $jsonString");
    }
}