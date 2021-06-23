package com.abaddon83.burraco.game.models.burracoGameendeds

import com.abaddon83.burraco.common.models.entities.Burraco
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card

data class BurracoPoint private constructor(
        override val identity: BurracoIdentity,
        override val cards: List<Card>
): Burraco() {
    constructor(burraco: Burraco): this(burraco.identity(),burraco.showCards())

}