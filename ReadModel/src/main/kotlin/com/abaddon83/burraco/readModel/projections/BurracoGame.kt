package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.utils.es.Event
import com.abaddon83.utils.es.Projection

data class BurracoGame(
    val identity: GameIdentity,
    val status: GameStatus,
    val deck: List<Card> = listOf(),
    val players: List<PlayerIdentity> = listOf(),
    val playerTurn: PlayerIdentity? = null,
    val numMazzettoAvailable: Int = 0,
    val discardPile: List<Card> = listOf()

) : Projection<BurracoGame>{

    constructor(): this(
        identity = GameIdentity(),
        status= GameStatus.Waiting
    )

    override fun applyEvent(e: Event): BurracoGame{
        return when(e){
            is BurracoGameCreated -> apply(e)
            is PlayerAdded -> apply(e)
            else -> this
        }
    }

    private fun apply(e: BurracoGameCreated): BurracoGame{
        check(this.identity.isEmpty())
        return BurracoGame(identity = e.identity, status = GameStatus.Waiting, players = listOf(), deck = e.deck)
    }

    private fun apply(e: PlayerAdded): BurracoGame{
        check(this.identity == e.identity)
        return copy(players = players.plus(e.playerIdentity))
    }


}

enum class GameStatus{
    Waiting,
    Execution,
    Ended
}