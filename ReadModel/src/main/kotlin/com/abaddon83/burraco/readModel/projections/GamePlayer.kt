package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Tris
import com.abaddon83.utils.es.Event
import com.abaddon83.utils.es.Projection


data class GamePlayer(
    val identity: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val handCards: List<Card> = listOf(),
    val tris: List<Tris> = listOf(),
    val scale: List<Scale> = listOf()
): Projection<GamePlayer>{

    constructor(): this(
        identity = PlayerIdentity() ,
        gameIdentity= GameIdentity()
    )

    override fun applyEvent(e: Event): GamePlayer{
        return when(e){
            is PlayerAdded -> apply(e)
            else -> this
        }
    }

    private fun apply(e: PlayerAdded): GamePlayer{
        check(this.identity.isEmpty())
        check(this.gameIdentity.isEmpty())

        return GamePlayer(identity = e.playerIdentity, gameIdentity = e.identity)
    }

}