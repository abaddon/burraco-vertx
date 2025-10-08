package com.abaddon83.burraco.player.command.playerDraft

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Card
import com.abaddon83.burraco.player.model.player.Player
import com.abaddon83.burraco.player.model.player.PlayerDraft
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class AddCardToPlayer(
    override val aggregateID: PlayerIdentity,
    val gameIdentity: GameIdentity,
    val card: Card
) : Command<Player>(aggregateID) {

    override fun execute(currentAggregate: Player?): Result<PlayerDraft> = runCatching {
        when (currentAggregate) {
            is PlayerDraft -> currentAggregate.addCard(aggregateID, gameIdentity, card)
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }

}
