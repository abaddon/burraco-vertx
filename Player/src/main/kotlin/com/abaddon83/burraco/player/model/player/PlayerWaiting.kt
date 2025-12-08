package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

/**
 * Represents a player waiting for their turn.
 *
 * In this state:
 * - The player is waiting for other players to complete their turns
 * - Cannot collect cards (per design decision - only PlayerDraft can collect cards)
 * - Can only observe game state
 *
 * State transitions:
 * - Future: PlayerWaiting → PlayerActive (via PlayerTurnStarted event - not yet implemented)
 */
data class PlayerWaiting(
    override val id: IIdentity,
    override val version: Long,
    override val gameIdentity: GameIdentity,
    override val user: String,
    val cards: List<Card>,
    val teamMateId: PlayerIdentity?
) : Player() {

    companion object Factory {
        fun empty(): PlayerWaiting = PlayerWaiting(
            PlayerIdentity.empty(),
            0,
            GameIdentity.empty(),
            "",
            emptyList(),
            null
        )
    }
}
