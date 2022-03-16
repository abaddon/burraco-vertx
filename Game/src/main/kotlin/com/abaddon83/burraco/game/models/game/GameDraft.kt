package com.abaddon83.burraco.game.models.game

import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.PlayerAdded
import com.abaddon83.burraco.game.helpers.GameConfig
import com.abaddon83.burraco.game.helpers.ValidationsTools.playerIsContained
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class GameDraft constructor(
    override val id: GameIdentity,
    override val version: Long,
    override val players: List<Player>
) : Game() {
    override val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
    override val uncommittedEvents: MutableCollection<IDomainEvent> = mutableListOf()

    fun addPlayer(playerIdentity: PlayerIdentity): GameDraft {
        require(!playerIsContained(playerIdentity,players)) { "The player $playerIdentity is already a player of game ${this.id.valueAsString()}" }
        val playersCount = players.size+1
        check(GameConfig.MAX_PLAYERS >= playersCount) { "Maximum number of players reached, (Max: ${GameConfig.MAX_PLAYERS})" }

        return raiseEvent(PlayerAdded.create(id, playerIdentity)) as GameDraft
    }

    fun dealCards(requestedBy: PlayerIdentity): GameWaitingDealer {
        require(players.size in GameConfig.MIN_PLAYERS..GameConfig.MAX_PLAYERS) { "Not enough players to deal the playing cards, ( Min players required: ${GameConfig.MIN_PLAYERS})" }

        require(playerIsContained(requestedBy,players)) { "The player $requestedBy is not one of the players " }
        return raiseEvent(CardDealingRequested.create(id, requestedBy)) as GameWaitingDealer
    }

    private fun apply(event: PlayerAdded): GameDraft {
        log.info("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        return copy(players = players.plus(Player(event.playerIdentity)))
    }

    private fun apply(event: CardDealingRequested): GameWaitingDealer {
        log.info("The aggregate is applying the event ${event::class.simpleName} with id ${event.messageId}")
        return GameWaitingDealer.from(this)
    }

}
