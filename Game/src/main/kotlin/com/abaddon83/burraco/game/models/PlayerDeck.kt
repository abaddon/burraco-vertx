package com.abaddon83.burraco.game.models

import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.utils.logs.WithLog

data class PlayerDeck constructor(override val cards: MutableList<Card>) : Deck, WithLog("MazzettoDeck") {

    override fun grabFirstCard(): Card = throw UnsupportedOperationException("This method is not implemented in the Mazzetto")

    override fun grabAllCards(): List<Card> = throw UnsupportedOperationException("This method is not implemented in the Mazzetto")

    fun getCardList(): List<Card> = cards.toList()

    companion object Factory {
        fun create(cards:List<Card>): PlayerDeck {
            require(cards.size == 11 || cards.size == 18){"Mazzetto Size is wrong, current size: ${cards.size}"}
            return PlayerDeck(cards.toMutableList())
        }

    }

}