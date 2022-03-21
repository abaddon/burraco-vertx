package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.game.helpers.TrisHelper.validTris
import com.abaddon83.burraco.game.models.card.Card

data class Tris private constructor(
    val id: TrisIdentity,
    val cards: List<Card>
){
    companion object Factory{
        fun create(id: TrisIdentity, cards: List<Card>): Tris {
            require(validTris(cards)){}
            return Tris(id,cards)
        }
    }
}
