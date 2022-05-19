package com.abaddon83.burraco.dealer.models

import java.util.*

object Suits {
    val allSuit: List<Suit> = listOf(Heart, Tile, Clover, Pike)

    fun valueOf(value: String): Suit =
        when (val suit = allSuit.plus(Jolly).find { it.javaClass.simpleName.lowercase() == value.lowercase() }) {
            is Suit -> suit
            else -> throw Exception("$value is not a valid Suit")
        }

    enum class Color{RED,BLACK}

    interface Suit {
        val icon: Char;
        val color: Color
        val label: String
    }

    object Heart : Suit {
        override val icon: Char = '\u2764'
        override val color: Color = Color.RED
        override val label: String = javaClass.simpleName.lowercase(Locale.getDefault())
    }

    object Tile : Suit {
        override val icon: Char = '\u2666'
        override val color: Color = Color.RED
        override val label: String = javaClass.simpleName.lowercase(Locale.getDefault())
    }

    object Clover : Suit {
        override val icon: Char = '\u2663'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.lowercase(Locale.getDefault())
    }

    object Pike : Suit {
        override val icon: Char = '\u2660'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.lowercase(Locale.getDefault())
    }

    object Jolly : Suit {
        override val icon: Char = '\u2660'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.lowercase(Locale.getDefault())
    }

}

