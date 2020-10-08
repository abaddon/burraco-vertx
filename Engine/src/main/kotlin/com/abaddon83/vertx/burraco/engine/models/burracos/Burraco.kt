package com.abaddon83.vertx.burraco.engine.models.burracos

import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.utils.ddd.Entity


abstract class Burraco(className: String) : Entity<BurracoIdentity>(className) {

    protected abstract val cards: List<Card>

    fun showCards(): List<Card> = cards

    fun isBurraco(): Boolean = cards.size >= 7

}