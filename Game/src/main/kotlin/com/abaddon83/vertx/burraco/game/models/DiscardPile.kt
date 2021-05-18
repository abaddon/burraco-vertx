package com.abaddon83.vertx.burraco.game.models

import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.vertx.burraco.game.models.decks.Deck
import com.abaddon83.utils.logs.WithLog

data class DiscardPile private constructor(override val cards: MutableList<Card>): Deck,WithLog("DiscardPile") {

    override fun grabAllCards(): List<Card> {
        check(cards.size >0){warnMsg("The DiscardPile is empty, you can't grab a card from here")}
        return super.grabAllCards()
    }

    override fun grabFirstCard(): Card = throw UnsupportedOperationException("You cannot grab only one card from the DiscardPile")


    fun addCard(card: Card): DiscardPile {
        cards.add(card)
        return this
    }

    fun showCards(): List<Card> = cards.toList()

    companion object Factory {
        fun create(cards: List<Card>): DiscardPile {
            return DiscardPile(cards = cards.toMutableList())
        }
    }
}