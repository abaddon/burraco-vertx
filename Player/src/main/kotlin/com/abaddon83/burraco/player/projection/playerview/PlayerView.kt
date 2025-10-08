package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.projections.IProjection
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class PlayerView(
    override val key: PlayerViewKey,
    val cards: List<Card>,
    val sequences: List<List<Card>>, // Future: sequences dropped on table
    val ranks: List<List<Card>>,     // Future: ranks dropped on table
    override val lastProcessedEvent: ConcurrentHashMap<String, Long>,
    override val lastUpdated: Instant?
) : IProjection {

    override fun applyEvent(event: IDomainEvent): IProjection {
        when (event) {
            is PlayerCollectedCard -> return apply(event)
            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.java.simpleName}")
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

    private fun apply(event: PlayerCollectedCard): PlayerView {
        // Validate that this event is for this player/game combination
        if (key.playerIdentity != PlayerIdentity.empty()) {
            check(this.key.playerIdentity == event.aggregateId) {
                "Player ID mismatch: projection key ${key.playerIdentity} vs event ${event.aggregateId}"
            }
            check(this.key.gameIdentity == event.gameId) {
                "Game ID mismatch: projection key ${key.gameIdentity} vs event ${event.gameId}"
            }
        }

        return this.copy(
            key = if (key.playerIdentity == PlayerIdentity.empty()) {
                PlayerViewKey(event.aggregateId, event.gameId)
            } else {
                key
            },
            cards = this.cards + event.card,
            lastUpdated = Instant.now()
        )
    }

    companion object {
        fun empty(): PlayerView = PlayerView(
            PlayerViewKey(PlayerIdentity.empty(), GameIdentity.empty()),
            emptyList(),
            emptyList(),
            emptyList(),
            ConcurrentHashMap(),
            Instant.now()
        )
    }
}
