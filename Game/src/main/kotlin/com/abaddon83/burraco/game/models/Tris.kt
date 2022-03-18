package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.models.card.Card

data class Tris(
    val id: TrisIdentity,
    val cards: List<Card>
)
