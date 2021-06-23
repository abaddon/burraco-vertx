package com.abaddon83.burraco.common.models.entities

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Ranks
import kotlinx.serialization.Serializable

@Serializable
data class BurracoTris(
        override val identity: BurracoIdentity,
        override val cards: List<Card>
    ): Burraco() {
        private val rank: Ranks.Rank

        init {
            check(cards.size >= 3) { "A tris is composed by 3 or more cards" }
            val rank = calculateTrisRank(cards)
            check(!listOf(Ranks.Two, Ranks.Jolly).contains(rank)) { "Tris of Jolly or Two are not allowed" }
            this.rank = rank
        }

    fun addCards(cardsToAdd: List<Card>): BurracoTris {
        return copy(cards = validateNewCards(cardsToAdd))
    }

    fun showRank(): Ranks.Rank = rank
    fun numCards():Int = cards.size

    private fun validateNewCards(cardsToValidate: List<Card>): List<Card> {
        val tmpCardList = cardsToValidate.plus(this.cards)
        val cardsWithoutJolly = tmpCardList.filterNot { c -> c.rank == Ranks.Jolly || c.rank == Ranks.Two }
        check((tmpCardList.minus(cardsWithoutJolly)).size <= 1) { warnMsg("A tris can contain at least 1 Jolly or Two") }
        check(cardsWithoutJolly.filterNot { c -> c.rank == rank }.isEmpty()) { warnMsg("A tris is composed by cards with the same rank") }
        return tmpCardList
    }

    companion object Factory {
        fun create(cards: List<Card>): BurracoTris {
            //check(cards.size >= 3) { "A tris is composed by 3 or more cards" }
            val rank = calculateTrisRank(cards)
            check(!listOf(Ranks.Two, Ranks.Jolly).contains(rank)) { "Tris of Jolly or Two are not allowed" }
            return BurracoTris(identity = BurracoIdentity.create(), cards = cards)
        }

        private fun calculateTrisRank(cards: List<Card>): Ranks.Rank {
            val cardsByRank = cards.groupBy { c -> c.rank }.mapValues { (k, v) -> v.size }

            val cardsByRankWithoutJollyAndTwo = cardsByRank.minus(Ranks.Jolly).minus(Ranks.Two)

            assert(cardsByRankWithoutJollyAndTwo.keys.size == 1) { "Too many different ranks found: ${cardsByRank.keys}" }
            return checkNotNull(cardsByRank.maxByOrNull { it.value }?.key) { "Tris Rank calculation failed" }
        }

    }

}