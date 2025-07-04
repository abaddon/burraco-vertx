package com.abaddon83.burraco.common.models.card

data class Card(
    val suit: Suit,
    val rank: Rank
) : Comparable<Card> {

    companion object Factory {
        fun fromLabel(label: String): Card {
            val elements = label.split("-")
            try {
                return Card(Suits.valueOf(elements[0]), Ranks.valueOf(elements[1]))
            } catch (e: Exception) {
                throw Exception("The $label is not a valid Card", e)
            }
        }

        fun jolly(): Card = Card(Suit.Jolly, Rank.Jolly)
    }

    override fun toString(): String {

        return "card: $label"
    }

    val label: String = "${suit.label}-${rank.label}"

    override fun compareTo(other: Card): Int {
        //   return 0 if two cards are equal
        //   return 1 if this card is greater than passed one
        //   return -1 otherwise
        val rankResult = rank.getRankComparator().compare(this.rank, other.rank)
        val suitResult = suit.compareTo(other.suit)

        return if (suitResult == 0) {
            rankResult
        } else {
            suitResult
        }
    }

}