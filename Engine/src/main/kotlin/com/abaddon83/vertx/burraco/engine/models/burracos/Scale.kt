package com.abaddon83.vertx.burraco.engine.models.burracos

import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Suits

abstract class Scale(className:String): Burraco(className) {

    fun numCards():Int = cards.size
    fun showSuit(): Suits.Suit = suit

    abstract fun addCards(cardsToAdd: List<Card>): Scale
    protected abstract val suit: Suits.Suit
    protected abstract fun validateNewCards(cardsToValidate: List<Card>): List<Card>

}