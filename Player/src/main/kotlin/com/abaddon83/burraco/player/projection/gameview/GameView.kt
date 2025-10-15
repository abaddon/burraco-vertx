package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.common.models.event.game.PlayerAdded
import com.abaddon83.burraco.player.projection.GameState
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.github.abaddon.kcqrs.core.projections.IProjection
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class GameView(
    override val key: GameViewKey,
    val players: List<PlayerIdentity>,
    val maxPlayers: Int,
    val state: GameState,
    override val lastProcessedEvent: ConcurrentHashMap<String, Long>,
    override val lastUpdated: Instant?
) : IProjection {

    /**
     * Checks if the game has capacity to accept more players
     * @return true if the game is in DRAFT state and has available slots
     */
    fun hasCapacity(): Boolean {
        return players.size < maxPlayers && state == GameState.DRAFT
    }

    /**
     * Checks if the game has reached maximum player capacity
     * @return true if the game is full
     */
    fun isFull(): Boolean = players.size >= maxPlayers

    override fun applyEvent(event: IDomainEvent): IProjection {
        return when (event) {
            is GameCreated -> apply(event)
            is PlayerAdded -> apply(event)
            else -> eventNotSupported(event)
        }
    }

    override fun withPosition(event: IDomainEvent): IProjection {
        val updatedPositions = lastProcessedEvent
        updatedPositions[event.aggregateType] = event.version

        return this.copy(
            lastProcessedEvent = updatedPositions,
            lastUpdated = Instant.now()
        )
    }

    private fun eventNotSupported(event: IDomainEvent): GameView {
        log.debug("Unsupported event type: ${event::class.java.simpleName}")
        return this;
    }

    private fun apply(event: GameCreated): GameView {
        check(this.key == GameViewKey(GameIdentity.empty())) { "Check failed, the key is not empty" }
        return this.copy(
            key = GameViewKey(event.aggregateId),
            players = emptyList(),
            maxPlayers = 4, // Burraco game constant
            state = GameState.DRAFT,
            lastUpdated = Instant.now()
        )
    }

    private fun apply(event: PlayerAdded): GameView {
        log.debug("Applying PlayerAdded event to GameView: {}", event.playerIdentity)

        // Idempotency check (in case event is replayed)
        if (players.contains(event.playerIdentity)) {
            log.warn("Player ${event.playerIdentity} already exists in GameView")
            return this
        }

        return this.copy(
            players = players + event.playerIdentity,
            lastUpdated = Instant.now()
        )
    }

    companion object {
        fun empty(): GameView = GameView(
            GameViewKey(GameIdentity.empty()),
            emptyList(),
            maxPlayers = 4,
            GameState.DRAFT,
            ConcurrentHashMap(),
            Instant.now()
        )
    }
}