package com.abaddon83.burraco.game.models.burracoGameWaitingPlayers

import com.abaddon83.burraco.common.events.GameInitialised
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.writeModel.UnsupportedEventException
import com.abaddon83.burraco.game.models.BurracoGame
import com.abaddon83.burraco.game.models.BurracoPlayer
import com.abaddon83.burraco.game.models.burracoWaitingDealer.BurracoGameWaitingDealer

data class BurracoGameWaitingPlayers constructor(
        override val identity: GameIdentity,
        override val players: List<BurracoPlayer>
        ) : BurracoGame(identity,"BurracoGameWaitingPlayers") {

    fun addPlayer(player: BurracoPlayer): BurracoGameWaitingPlayers {
        check(players.size < maxPlayers) {
            warnMsg("Maximum number of players reached, (Max: ${maxPlayers})")
        }
        check(!isAlreadyAPlayer(player.identity())) {
            warnMsg("The player ${player.identity()} is already a player of game ${this.identity()}")
        }
        return applyAndQueueEvent(PlayerAdded(identity = identity, playerIdentity = player.identity()))
    }

    fun isAlreadyAPlayer(playerIdentity: PlayerIdentity): Boolean {
        return players.find { p -> p.identity() == playerIdentity } != null
    }

    fun start(): BurracoGameWaitingDealer {
        check(players.size in 2..4) {
            warnMsg("Not enough players to initiate the game, ( Min: ${minPlayers})")
        }

        return applyAndQueueEvent(GameInitialised(
                identity = identity(), players = players.map { it.identity() })
        )
    }

    override fun applyEvent(event: Event): BurracoGame {
        log.info("apply event: ${event::class.simpleName.toString()}")
        return when (event) {
            is GameInitialised -> apply(event)
            is PlayerAdded -> apply(event)
            else -> throw UnsupportedEventException(event::class.java)
        }
    }

    private fun apply(event: PlayerAdded): BurracoGameWaitingPlayers {
        return copy(players = players.plus(BurracoPlayer(event.playerIdentity)))
    }

    private fun apply(event: GameInitialised): BurracoGameWaitingDealer {
        check(event.identity == identity){ "Game Identity mismatch" }

        val burracoGameWaitingDealer = BurracoGameWaitingDealer.create(
                identity = identity(),
                players = players,//.map { p -> PlayerInGame.create(p.identity(),p.cards) },
        )
        return burracoGameWaitingDealer
    }

}
