package com.abaddon83.burraco.common.models.card

import com.abaddon83.burraco.common.models.card.Suit.Clover
import com.abaddon83.burraco.common.models.card.Suit.Heart
import com.abaddon83.burraco.common.models.card.Suit.Pike
import com.abaddon83.burraco.common.models.card.Suit.Tile

enum class Suit(val label: String) {
    Heart("Heart"), Tile("Tile"), Clover("Clover"), Pike("Pike"), Jolly("Jolly");
}

object Suits {
    val allSuit: List<Suit> = listOf(Heart, Tile, Clover, Pike)
    private val labelsMap = listOf(allSuit, listOf(Suit.Jolly)).flatten().associateBy { it.label }

    fun valueOf(label: String): Suit =
        labelsMap[label] ?: throw IllegalArgumentException("No suit found for label: $label")

}

