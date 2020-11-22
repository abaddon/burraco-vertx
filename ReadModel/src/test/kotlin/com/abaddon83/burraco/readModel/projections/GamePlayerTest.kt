package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import org.junit.Test
import kotlin.test.assertEquals

internal class GamePlayerTest {

    @Test
    fun applyEventPlayerAdded() {
        val gamePlayer = GamePlayer();
        val playerAdded = PlayerAdded(identity = GameIdentity.create(), playerIdentity = PlayerIdentity.create())

        val newGamePlayer = gamePlayer.applyEvent(playerAdded)

        assertEquals(playerAdded.playerIdentity,newGamePlayer.identity,"Player identity is not the same")
        assertEquals(playerAdded.identity,newGamePlayer.gameIdentity,"Game identity is not the same")

    }
}