package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.common.models.TrisIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.game.helpers.TrisHelper.validTris
import com.abaddon83.burraco.game.helpers.containsJolly

data class Tris(
    val id: TrisIdentity,
    override val cards: List<Card>
) : Burraco {

    companion object Factory {
        fun create(id: TrisIdentity, cards: List<Card>): Tris {
            require(validTris(cards)) {}
            return Tris(id, cards)
        }
    }

    override fun score(): Int = when {
        isBurraco() && cards.count { it.rank == Rank.Two } > 1 && !cards.containsJolly() -> 200
        isBurraco() && cards.count { it.rank == Rank.Two } == 0 && !cards.containsJolly() -> 200
        isBurraco() && cards.count { it.rank == Rank.Two } == 1 || cards.containsJolly() && cards.size > 7 -> 150
        isBurraco() && cards.count { it.rank == Rank.Two } == 1 || cards.containsJolly() && cards.size == 7 -> 100
        else -> 0
    }


}
