package com.abaddon83.vertx.burraco.engine.models.burracoGameendeds

import com.abaddon83.vertx.burraco.engine.models.burracos.Burraco
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card

data class BurracoPoint private constructor(
        override val identity: BurracoIdentity,
        override val cards: List<Card>
): Burraco("BurracoPoint") {
    constructor(burraco: Burraco): this(burraco.identity(),burraco.showCards())

}