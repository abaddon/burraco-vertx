package com.abaddon83.burraco.player.model.player

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import io.github.abaddon.kcqrs.core.IIdentity
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

/**
 * Represents a player in active turn state.
 *
 * In this state:
 * - The player is currently playing their turn
 * - Cannot collect cards (per design decision - only PlayerDraft can collect cards)
 * - Future: Will implement turn actions (draw, meld, discard)
 *
 * State transitions:
 * - Future: PlayerActive → PlayerWaiting (via PlayerTurnEnded event - not yet implemented)
 */
data class PlayerActive(
    override val id: IIdentity,
    override val version: Long,
    override val gameIdentity: GameIdentity,
    override val user: String,
    val cards: List<Card>,
    val teamMateId: PlayerIdentity?
) : Player() {

    companion object Factory {
        fun empty(): PlayerActive = PlayerActive(
            PlayerIdentity.empty(),
            0,
            GameIdentity.empty(),
            "",
            emptyList(),
            null
        )
    }
}
