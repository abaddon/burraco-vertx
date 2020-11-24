package com.abaddon83.burraco.common.models.valueObjects

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.utils.ddd.writeModel.Entity


abstract class Burraco() : Entity<BurracoIdentity>() {

    protected abstract val cards: List<Card>

    fun showCards(): List<Card> = cards

    fun isBurraco(): Boolean = cards.size >= 7

}