package com.abaddon83.burraco.dealer.models

import com.abaddon83.burraco.dealer.models.Suit.*

enum class Suit(val label: String){
    Heart("Heart"), Tile("Tile"), Clover("Clover"), Pike("Pike"), Jolly("Jolly")
}

object Suits {
    val allSuit: List<Suit> = listOf(Heart, Tile, Clover, Pike)

    fun valueOf(value: String): Suit = Suit.valueOf(value)

}

