package com.abaddon83.burraco.player.projection.gameview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import com.abaddon83.burraco.player.projection.GameState
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import io.github.abaddon.kcqrs.core.projections.IProjection
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class GameView(
    override val key: GameViewKey,
    val players: List<PlayerIdentity>,
    val state: GameState,
    override val lastProcessedEvent: ConcurrentHashMap<String, Long>,
    override val lastUpdated: Instant?
) : IProjection {

    override fun applyEvent(event: IDomainEvent): IProjection {
        return when (event) {
            is GameCreated -> apply(event)
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
            state = GameState.DRAFT,
            lastUpdated = Instant.now()
        )
    }

    companion object {
        fun empty(): GameView = GameView(
            GameViewKey(GameIdentity.empty()),
            emptyList(),
            GameState.DRAFT,
            ConcurrentHashMap(),
            Instant.now()
        )
    }
}