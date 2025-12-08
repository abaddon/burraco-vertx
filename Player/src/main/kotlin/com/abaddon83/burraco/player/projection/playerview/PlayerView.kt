package com.abaddon83.burraco.player.projection.playerview

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.common.models.event.player.PlayerActivated
import com.abaddon83.burraco.common.models.event.player.PlayerCollectedCard
import com.abaddon83.burraco.common.models.event.player.PlayerCreated
import com.abaddon83.burraco.common.models.event.player.PlayerWaitingTurn
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import io.github.abaddon.kcqrs.core.projections.IProjection
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class PlayerView(
    override val key: PlayerViewKey,
    val cards: List<Card>,
    val sequences: List<List<Card>>, // Future: sequences dropped on table
    val ranks: List<List<Card>>,     // Future: ranks dropped on table
    val isMyTurn: Boolean = false,   // Indicates if it's this player's turn
    val teamMateId: PlayerIdentity? = null, // The player's teammate
    override val lastProcessedEvent: ConcurrentHashMap<String, Long>,
    override val lastUpdated: Instant?
) : IProjection {

    override fun applyEvent(event: IDomainEvent): IProjection {
        return when (event) {
            is PlayerCreated -> apply(event)
            is PlayerCollectedCard -> apply(event)
            is PlayerActivated -> apply(event)
            is PlayerWaitingTurn -> apply(event)
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

    private fun eventNotSupported(event: IDomainEvent): PlayerView {
        log.debug("Unsupported event type: ${event::class.java.simpleName}")
        return this;
    }

    private fun apply(event: PlayerCreated): PlayerView {
        // Validate that this event is for this player/game combination
        if (key.playerIdentity != PlayerIdentity.empty()) {
            check(this.key.playerIdentity == event.aggregateId) {
                "Player ID mismatch: projection key ${key.playerIdentity} vs event ${event.aggregateId}"
            }
            check(this.key.gameIdentity == event.gameIdentity) {
                "Game ID mismatch: projection key ${key.gameIdentity} vs event ${event.gameIdentity}"
            }
        }
        return this.copy(
            key = PlayerViewKey(event.aggregateId, event.gameIdentity),
            lastUpdated = Instant.now()
        )
    }

    private fun apply(event: PlayerCollectedCard): PlayerView {
        // Only validate IDs if projection key is not empty (allow initialization from events)
        if (key.playerIdentity != PlayerIdentity.empty()) {
            check(this.key.playerIdentity == event.aggregateId) {
                "Player ID mismatch: projection key ${key.playerIdentity} vs event ${event.aggregateId}"
            }
            check(this.key.gameIdentity == event.gameId) {
                "Game ID mismatch: projection key ${key.gameIdentity} vs event ${event.gameId}"
            }
        }

        return this.copy(
            key = if (key.playerIdentity == PlayerIdentity.empty()) PlayerViewKey(event.aggregateId, event.gameId) else key,
            cards = this.cards + event.card,
            lastUpdated = Instant.now()
        )
    }

    private fun apply(event: PlayerActivated): PlayerView {
        log.debug("Applying PlayerActivated event to PlayerView: player {} is now active", event.aggregateId)

        check(this.key.playerIdentity == event.aggregateId) {
            "Player ID mismatch: projection key ${key.playerIdentity} vs event ${event.aggregateId}"
        }
        check(this.key.gameIdentity == event.gameIdentity) {
            "Game ID mismatch: projection key ${key.gameIdentity} vs event ${event.gameIdentity}"
        }

        return this.copy(
            isMyTurn = true,
            teamMateId = event.teamMateId,
            lastUpdated = Instant.now()
        )
    }

    private fun apply(event: PlayerWaitingTurn): PlayerView {
        log.debug("Applying PlayerWaitingTurn event to PlayerView: player {} is waiting", event.aggregateId)

        check(this.key.playerIdentity == event.aggregateId) {
            "Player ID mismatch: projection key ${key.playerIdentity} vs event ${event.aggregateId}"
        }
        check(this.key.gameIdentity == event.gameIdentity) {
            "Game ID mismatch: projection key ${key.gameIdentity} vs event ${event.gameIdentity}"
        }

        return this.copy(
            isMyTurn = false,
            teamMateId = event.teamMateId,
            lastUpdated = Instant.now()
        )
    }

    companion object {
        fun empty(): PlayerView = PlayerView(
            PlayerViewKey(PlayerIdentity.empty(), GameIdentity.empty()),
            emptyList(),
            emptyList(),
            emptyList(),
            false,
            null,
            ConcurrentHashMap(),
            Instant.now()
        )
    }
}
