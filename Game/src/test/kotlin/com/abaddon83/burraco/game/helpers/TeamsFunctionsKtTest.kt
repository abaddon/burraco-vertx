package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Team
import com.abaddon83.burraco.common.models.PlayerIdentity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TeamsFunctionsKtTest{

    val PLAYER_ID1 = PlayerIdentity.create()
    val PLAYER_ID2 = PlayerIdentity.create()
    val PLAYER_ID3 = PlayerIdentity.create()
    val PLAYER_ID4 = PlayerIdentity.create()

    @Test
    fun `Given a list of Teams composed from multiple players when a player_id1'team is requested then team1 returned`() {
        val expected = Team(listOf(PLAYER_ID1,PLAYER_ID2))
        val list = listOf(Team(listOf(PLAYER_ID3,PLAYER_ID4))).plus(expected)

        val actual = list.playerTeam(PLAYER_ID1)
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of Teams composed from multiple players when a playe_id5 is requested then nothing returned`() {
        val expected = null
        val list = listOf(
            Team(listOf(PLAYER_ID1,PLAYER_ID2)),
            Team(listOf(PLAYER_ID3,PLAYER_ID4))
        )

        val actual = list.playerTeam(PlayerIdentity.create())
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of teams when one team is updated then the list contain the update`() {
        val expected = listOf(
            Team(listOf(PLAYER_ID1,PLAYER_ID2)),
            Team(listOf(PLAYER_ID3,PLAYER_ID4),true)
        )

        val list = listOf(
            Team(listOf(PLAYER_ID1,PLAYER_ID2)),
            Team(listOf(PLAYER_ID3,PLAYER_ID4))
        )

        val actual = list.updateTeam(PLAYER_ID3){ team ->
            team.copy(playerDeckPickedUp = true)
        }
        assertEquals(expected, actual)
    }

    @Test
    fun `Given a list of teams when one team not in list is updated then the list is not updated`() {

        val expected = listOf(
            Team(listOf(PLAYER_ID1,PLAYER_ID2)),
            Team(listOf(PLAYER_ID3,PLAYER_ID4))
        )

        val actual = expected.updateTeam(PlayerIdentity.create()){ team ->
            team.copy(playerDeckPickedUp = true)
        }
        assertEquals(expected, actual)
    }


}