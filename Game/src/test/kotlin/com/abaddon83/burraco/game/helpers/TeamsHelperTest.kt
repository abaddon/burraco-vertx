package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class TeamsHelperTest{

    val PLAYER_ID1 = PlayerIdentity.create()
    val PLAYER_ID2 = PlayerIdentity.create()
    val PLAYER_ID3 = PlayerIdentity.create()
    val PLAYER_ID4 = PlayerIdentity.create()

    @Test
    fun `Given a list of 4 players when build teams then exception`() {

        val playerList= listOf(PLAYER_ID1,PLAYER_ID2,PLAYER_ID3,PLAYER_ID4)

        assertThrows(IllegalArgumentException::class.java){
            TeamsHelper.buildTeamsWith3Players(PLAYER_ID1,playerList)
        }
    }

    @Test
    fun `Given a list of 3 players with selected player wrong when build teams then exception`() {

        val playerList= listOf(PLAYER_ID1,PLAYER_ID2,PLAYER_ID3)

        assertThrows(IllegalArgumentException::class.java){
            TeamsHelper.buildTeamsWith3Players(PLAYER_ID4,playerList)
        }
    }

    @Test
    fun `Given a list of 3 players with valid player when build teams then a list of teams is created`() {

        val expected = listOf(
            Team(listOf(PLAYER_ID1), true),
            Team(listOf(PLAYER_ID2,PLAYER_ID3),false)
        )
        val playerList= listOf(PLAYER_ID1,PLAYER_ID2,PLAYER_ID3)

        val actual = TeamsHelper.buildTeamsWith3Players(PLAYER_ID1,playerList)
        assertEquals(expected,actual)
    }

}