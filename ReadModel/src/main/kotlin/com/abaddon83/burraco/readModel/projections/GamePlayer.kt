package com.abaddon83.burraco.readModel.projections

import com.abaddon83.burraco.common.events.CardAssignedToPlayer
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Tris
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.Projection


data class GamePlayer(
    override val key: GamePlayerKey,
    val identity: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val handCards: List<Card> = listOf(),
    val tris: List<Tris> = listOf(),
    val scale: List<Scale> = listOf(),

): Projection<GamePlayer> {

    constructor(): this(
        key = GamePlayerKey(PlayerIdentity(),GameIdentity()),
        identity = PlayerIdentity() ,
        gameIdentity= GameIdentity()
    )
    constructor(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity,): this(
        key = GamePlayerKey(playerIdentity,gameIdentity),
        identity = playerIdentity ,
        gameIdentity= gameIdentity
    )


    override fun applyEvent(event: Event): GamePlayer{
        return when(event){
            is PlayerAdded -> apply(event)
            is CardAssignedToPlayer -> apply(event)
            else -> this
        }
    }

    private fun apply(e: PlayerAdded): GamePlayer{
        check(this.identity.isEmpty())
        check(this.gameIdentity.isEmpty())

        return GamePlayer(playerIdentity = e.playerIdentity, gameIdentity = e.identity)
    }

    private fun apply(e: CardAssignedToPlayer): GamePlayer{
        check(this.key == GamePlayerKey(e.player,e.identity)){" check failed, the key is not the same"}
        return copy(handCards = handCards.plus(e.card))
    }



}