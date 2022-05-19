package com.abaddon83.burraco.dealer.models


data class Card(
    val suit: Suits.Suit,
    val rank: Ranks.Rank
) {

//    companion object Factory{
//        fun fromLabel(label: String): Card {
//            val elements = label.split("-")
//            try {
//                return Card(Suits.valueOf(elements[0]), Ranks.valueOf(elements[1]))
//            }catch (e: Exception){
//                throw Exception("The $label is not a valid Card",e)
//            }
//        }
//
//        fun jolly():Card = Card(Suits.Jolly,Ranks.Jolly)
//    }

    override fun toString(): String {

        return "card: ${suit.icon} - ${rank.label}"
    }

//    val label: String = "${suit.label}-${rank.label}"

}







