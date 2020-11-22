package com.abaddon83.burraco.common.models.valueObjects

import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import kotlinx.serialization.Serializable

@Serializable
open class Tris(override val identity: BurracoIdentity,
                val rank: Ranks.Rank,
                override val cards: List<Card>

) : Burraco(){

    fun showRank(): Ranks.Rank = rank
    fun numCards():Int = cards.size
}


