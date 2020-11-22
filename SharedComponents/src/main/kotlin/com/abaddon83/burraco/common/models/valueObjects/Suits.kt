package com.abaddon83.burraco.common.models.valueObjects

import com.abaddon83.burraco.common.serializations.custom.SuitCustomSerializer
import kotlinx.serialization.Serializable
import java.lang.Exception

object Suits {
    val allSuit: List<Suit> = listOf(Heart, Tile, Clover, Pike)

    fun valueOf(value: String): Suit =
        when (val suit = allSuit.plus(Jolly).find { it.javaClass.simpleName.toLowerCase() == value.toLowerCase() }) {
            is Suit -> suit
            else -> throw Exception("$value is not a valid Suit")
        }

    enum class Color{RED,BLACK}

    @Serializable( with = SuitCustomSerializer::class)
    interface Suit {
        val icon: Char;
        val color: Color
        val label: String
    }

    object Heart : Suit {
        override val icon: Char = '\u2764'
        override val color: Color = Color.RED
        override val label: String = javaClass.simpleName.toLowerCase()
    }

    object Tile : Suit {
        override val icon: Char = '\u2666'
        override val color: Color = Color.RED
        override val label: String = javaClass.simpleName.toLowerCase()
    }

    object Clover : Suit {
        override val icon: Char = '\u2663'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.toLowerCase()
    }

    object Pike : Suit {
        override val icon: Char = '\u2660'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.toLowerCase()
    }

    object Jolly : Suit {
        override val icon: Char = '\u2660'
        override val color: Color = Color.BLACK
        override val label: String = javaClass.simpleName.toLowerCase()
    }

}

