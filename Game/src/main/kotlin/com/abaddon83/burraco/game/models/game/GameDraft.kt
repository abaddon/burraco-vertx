package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.contains
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.game.models.player.WaitingPlayer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameDraft constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<WaitingPlayer>
) : Game() {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)

    companion object Factory {
        fun init(id: GameIdentity): GameDraft = GameDraft(id, 0, listOf())
    }

    fun addPlayer(playerIdentity: PlayerIdentity): GameDraft {
        require(!players.contains(playerIdentity)) { "The player ${playerIdentity.valueAsString()} is already a player of game ${this.id.valueAsString()}" }
        val playersCount = players.size+1
        check(GameConfig.MAX_PLAYERS >= playersCount) { "Maximum number of players reached, (Max: ${GameConfig.MAX_PLAYERS})" }

        return raiseEvent(PlayerAdded.create(id, playerIdentity)) as GameDraft
    }

    fun requestDealCards(requestedBy: PlayerIdentity): GameWaitingDealer {
        require(players.contains(requestedBy)) { "The player $requestedBy is not one of the players " }
        check(players.size in GameConfig.MIN_PLAYERS..GameConfig.MAX_PLAYERS) { "Not enough players to deal the playing cards, ( Min players required: ${GameConfig.MIN_PLAYERS})" }

        return raiseEvent(CardDealingRequested.create(id, requestedBy)) as GameWaitingDealer
    }

    private fun apply(event: PlayerAdded): GameDraft {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        val updatedPlayers=players.plus(WaitingPlayer(event.playerIdentity))
        log.debug("New Player added, now there are ${updatedPlayers.size} players")
        return copy(players = updatedPlayers )
    }

    private fun apply(event: CardDealingRequested): GameWaitingDealer {
        log.debug("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        return GameWaitingDealer.from(this)
    }

}
