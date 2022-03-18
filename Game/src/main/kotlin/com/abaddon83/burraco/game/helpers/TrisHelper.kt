package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks

object TrisHelper {

    fun validTris(cards: List<Card>): Boolean{
        check(cards.size >= 3) { "A tris is composed by 3 or more cards" }
        val rank = calculateTrisRank(cards)
        check(!listOf(Ranks.Two, Ranks.Jolly).contains(rank)) { "Tris of Jolly or Two are not allowed" }
        return true
    }

    private fun calculateTrisRank(cards: List<Card>): Ranks.Rank {
        val cardsByRank = cards.groupBy { c -> c.rank }.mapValues { (k, v) -> v.size }

        val cardsByRankWithoutJollyAndTwo = cardsByRank.minus(Ranks.Jolly).minus(Ranks.Two)

        assert(cardsByRankWithoutJollyAndTwo.keys.size == 1) { "Too many different ranks found: ${cardsByRank.keys}" }
        return checkNotNull(cardsByRank.maxByOrNull { it.value }?.key) { "Tris Rank calculation failed" }
    }

}