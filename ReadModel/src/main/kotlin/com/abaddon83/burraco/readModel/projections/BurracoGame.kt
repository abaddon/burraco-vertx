package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.GameInitialised
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.Projection

data class BurracoGame(
    override val key: BurracoGameKey,
    val identity: GameIdentity,
    val status: GameStatus,
    val deck: List<Card> = listOf(),
    val players: List<PlayerIdentity> = listOf(),
    val playerTurn: PlayerIdentity? = null,
    val numMazzettoAvailable: Int = 0,
    val discardPile: List<Card> = listOf()

) : Projection<BurracoGame> {

    constructor(): this(
        key = BurracoGameKey(GameIdentity()),
        identity = GameIdentity(),
        status= GameStatus.Waiting
    )

    constructor(identity: GameIdentity, status: GameStatus,players: List<PlayerIdentity>,deck: List<Card>): this(
        key = BurracoGameKey(identity),
        identity = identity,
        status = status,
        players = players,
        deck = deck
    )

    override fun applyEvent(event: Event): BurracoGame{
        return when(event){
            is BurracoGameCreated -> apply(event)
            is PlayerAdded -> apply(event)
            is GameInitialised -> apply(event)
            else -> this
        }
    }

    private fun apply(e: BurracoGameCreated): BurracoGame{
        check(this.identity.isEmpty())
        return BurracoGame(identity = e.identity, status = GameStatus.Waiting, players = listOf(), deck = e.deck)
    }

    private fun apply(e: PlayerAdded): BurracoGame{
        check(this.key == BurracoGameKey(e.identity)){" check failed, the key is not the same"}
        return copy(players = players.plus(e.playerIdentity))
    }

    private fun apply(e: GameInitialised): BurracoGame{
        check(this.key == BurracoGameKey(e.identity)){" check failed, the key is not the same"}
        return copy(
            status = GameStatus.Execution,
            deck = e.deck,
            playerTurn = e.playerTurn,
            numMazzettoAvailable = 2,
            discardPile = e.discardPileCards
            )
    }

}

enum class GameStatus{
    Waiting,
    Execution,
    Ended
}
inline fun GameStatus.toJson(): String = this.toString()


