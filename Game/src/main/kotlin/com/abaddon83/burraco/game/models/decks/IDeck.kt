package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.models.card.Card


interface IDeck{
    val cards: List<Card>

    fun numCards(): Int = cards.size

}