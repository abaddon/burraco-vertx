package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.common.helpers.log
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.card.Ranks

object TrisHelper {
    fun validTris(cards: List<Card>): Boolean {
        if (cards.size < 3) {
            log.warn("A tris is composed by 3 or more cards")
            return false
        }
        return when(val rank=calculateTrisRank(cards)) {
            is Ranks.Rank -> {
                if (listOf(Ranks.Two, Ranks.Jolly).contains(rank)) {
                    log.warn("Tris of Jolly or Two are not allowed")
                    false
                } else
                    true
            }
            else -> false
        }
    }

    private fun calculateTrisRank(cards: List<Card>): Ranks.Rank? {
        val cardsByRank = cards.groupBy { c -> c.rank }.mapValues { (k, v) -> v.size }
        val cardsByRankWithoutJollyAndTwo = cardsByRank.minus(Ranks.Jolly).minus(Ranks.Two)
        if (cardsByRankWithoutJollyAndTwo.keys.size > 1) {
            log.warn("Too many different ranks found: ${cardsByRank.keys}")
            return null
        }
        return when (val rank = cardsByRank.maxByOrNull { it.value }?.key) {
            is Ranks.Rank -> rank
            else -> null
        }
    }

}