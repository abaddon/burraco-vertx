package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.helpers.StraightHelper.validStraight
import com.abaddon83.burraco.game.helpers.TrisHelper.validTris
import com.abaddon83.burraco.game.models.card.Card

data class Straight private constructor(
    val id: StraightIdentity,
    val cards: List<Card>
){
    companion object Factory{
        fun create(id: StraightIdentity, cards: List<Card>): Straight {
            require(validStraight(cards)){}
            return Straight(id,cards)
        }
    }
}
