package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.game.models.card.Card

interface Player {
    val id: PlayerIdentity
    val cards: List<Card>
}