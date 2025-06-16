package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameCreated
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.events.game.PlayerRemoved
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.contains
import com.abaddon83.burraco.game.models.player.WaitingPlayer
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log

private const val AGGREGATE_APPLY_EVENT_MSG = "The aggregate is applying the event {} with id {} to the aggregate {}"
private const val VALIDATION_MSG_GAME_EXIST = "Current game with id %s is already created"
private const val VALIDATION_MSG_PLAYER_ALREADY_ADDED = "The player %s is already a player of game %s"


data class GameDraft constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<WaitingPlayer>
) : Game() {

    companion object Factory {
        fun empty(): GameDraft = GameDraft(GameIdentity.empty(), 0, listOf())
    }

    fun createGame(gameIdentity: GameIdentity): GameDraft {
        check(this.id == GameIdentity.empty()) { String.format(VALIDATION_MSG_GAME_EXIST, this.id) }
        return raiseEvent(GameCreated.create(gameIdentity)) as GameDraft
    }

    fun addPlayer(playerIdentity: PlayerIdentity): GameDraft {
        require(!players.contains(playerIdentity)) {
            String.format(
                VALIDATION_MSG_PLAYER_ALREADY_ADDED,
                playerIdentity.valueAsString(),
                this.id.valueAsString()
            )
        }
        val playersCount = players.size + 1
        check(GameConfig.MAX_PLAYERS >= playersCount) { "Maximum number of players reached, (Max: ${GameConfig.MAX_PLAYERS})" }

        return raiseEvent(PlayerAdded.create(id, playerIdentity)) as GameDraft
    }

    fun removePlayer(playerIdentity: PlayerIdentity): GameDraft {
        require(players.contains(playerIdentity)) { "The player ${playerIdentity.valueAsString()} is not a player of game ${this.id.valueAsString()}" }

        return raiseEvent(PlayerRemoved.create(id, playerIdentity)) as GameDraft
    }

    fun requestDealCards(requestedBy: PlayerIdentity): GameWaitingDealer {
        require(players.contains(requestedBy)) { "The player $requestedBy is not one of the players " }
        check(players.size in GameConfig.MIN_PLAYERS..GameConfig.MAX_PLAYERS) { "Not enough players to deal the playing cards, ( Min players required: ${GameConfig.MIN_PLAYERS})" }

        return raiseEvent(CardDealingRequested.create(id, requestedBy)) as GameWaitingDealer
    }

    private fun apply(event: GameCreated): GameDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.simpleName, event.messageId, event.aggregateId)
        return copy(id = event.aggregateId, version = 0)
    }

    private fun apply(event: PlayerAdded): GameDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.simpleName, event.messageId, event.aggregateId)
        val updatedPlayers = players.plus(WaitingPlayer(event.playerIdentity))
        log.debug("New Player added, now there are ${updatedPlayers.size} players")
        return copy(players = updatedPlayers, version = version + 1)
    }

    private fun apply(event: PlayerRemoved): GameDraft {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.simpleName, event.messageId, event.aggregateId)
        val updatedPlayers = players.minus(WaitingPlayer(event.playerIdentity))
        log.debug("Player removed, now there are ${updatedPlayers.size} players")
        return copy(players = updatedPlayers, version = version + 1)
    }

    private fun apply(event: CardDealingRequested): GameWaitingDealer {
        log.debug(AGGREGATE_APPLY_EVENT_MSG, event::class.simpleName, event.messageId, event.aggregateId)
        return GameWaitingDealer.from(this.copy(version = version + 1))
    }

}
