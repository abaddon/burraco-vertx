package com.abaddon83.burraco.game.models.card

import com.abaddon83.burraco.game.helpers.GameConfig.SCORE

data class Card(
    val suit: Suits.Suit,
    val rank: Ranks.Rank
) : Comparable<Card> {

    companion object Factory{
        fun fromLabel(label: String): Card {
            val elements = label.split("-")
            try {
                return Card(Suits.valueOf(elements[0]), Ranks.valueOf(elements[1]))
            }catch (e: Exception){
                throw Exception("The $label is not a valid Card",e)
            }
        }

        fun jolly():Card = Card(Suits.Jolly,Ranks.Jolly)
    }

    fun score(): Int = SCORE.getOrDefault(rank,0)

    override fun toString(): String {

        return "card: ${suit.icon} - ${rank.label}"
    }

    val label: String = "${suit.label}-${rank.label}"

    override fun compareTo(other: Card): Int {
        //   return 0 if two cards are equal
        //   return 1 if this card is greater than passed one
        //   return -1 otherwise
        if (rank.position > other.rank.position) {
            return -1
        } else if (rank.position == other.rank.position) {
            return 0
        } else {
            return 1
        }
    }

}







