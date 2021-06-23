package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.game.models.players.Player

data class BurracoPlayer(
    override val identity: PlayerIdentity,
    val cards: List<Card> = listOf()
) : Player() {
}
