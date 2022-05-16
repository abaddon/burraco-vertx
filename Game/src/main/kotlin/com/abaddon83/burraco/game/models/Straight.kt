package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.helpers.StraightHelper.sortStraight
import com.abaddon83.burraco.game.helpers.StraightHelper.validStraight
import com.abaddon83.burraco.game.models.card.Card
import kotlin.math.absoluteValue

data class Straight private constructor(
    val id: StraightIdentity,
    override val cards: List<Card>
) : Burraco {

    companion object Factory {
        fun create(id: StraightIdentity, cards: List<Card>): Straight {
            require(validStraight(cards)) { "These cards are not a valid Straight" }
            return sortStraight(Straight(id, cards))
        }
    }

    override fun score(): Int {
        var jollyAvailable = false
        var numCardsAfterJolly = 0
        var previousPosition = 0
        for ((index, card) in cards.withIndex()) {
            val compareIndex = if (index + 1 == cards.size) index - 1 else index + 1
            when {
                card.rank.position > 0 && (card.rank.position - cards[compareIndex].rank.position).absoluteValue != 1 -> {
                    //previousPosition = 0
                    numCardsAfterJolly = 0
                    jollyAvailable = true
                }
                card.rank.position == 0 -> {
                    //previousPosition = card.rank.position
                    numCardsAfterJolly = 0
                    jollyAvailable = true
                }
                card.rank.position > 0 && (card.rank.position - cards[compareIndex].rank.position).absoluteValue == 1 -> {
                    //previousPosition = card.rank.position
                    numCardsAfterJolly += 1
                }
                previousPosition == 0 /*&& card.rank.position != 0 && card.rank.position == cards[index+1].rank.position -1*/ -> {
                    //previousPosition = card.rank.position
                    numCardsAfterJolly += 1
                }

            }
        }
        return when {
            isBurraco() && jollyAvailable && numCardsAfterJolly >= 7 -> 150
            isBurraco() && jollyAvailable && numCardsAfterJolly < 7 -> 100
            isBurraco() && !jollyAvailable -> 200
            else -> 0
        }
    }

}
