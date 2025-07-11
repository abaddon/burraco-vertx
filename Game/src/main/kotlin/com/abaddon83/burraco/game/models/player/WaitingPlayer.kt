package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card

data class WaitingPlayer(
    override val id: PlayerIdentity,
    override val cards: List<Card> = listOf()
): Player