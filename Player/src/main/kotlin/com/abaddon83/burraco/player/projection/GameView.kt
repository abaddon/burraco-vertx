package com.abaddon83.burraco.player.projection

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.event.game.GameCreated
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
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
        when (event) {
            is GameCreated -> return apply(event)
            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.java.simpleName}")
        }
    }

    override fun withPosition(event: IDomainEvent): IProjection {
        TODO("Not yet implemented")
    }

    private fun apply(event: GameCreated): GameView {
        check(this.key == GameViewKey(GameIdentity.empty())) { "Check failed, the key is not empty" }
        return GameView(
            key = GameViewKey(event.aggregateId),
            players = emptyList(),
            state = GameState.DRAFT,
            lastProcessedEvent = ConcurrentHashMap(),
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