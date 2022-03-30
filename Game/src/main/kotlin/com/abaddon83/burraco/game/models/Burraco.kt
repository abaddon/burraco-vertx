package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.models.card.Card

interface Burraco {
    val cards: List<Card>
    fun isBurraco():Boolean = cards.size >= 7
    fun score(): Int
}