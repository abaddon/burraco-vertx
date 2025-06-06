package com.abaddon83.burraco.game.commands.gameDraft

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class RequestDealCards(
    override val aggregateID: GameIdentity,
    val requestedBy: PlayerIdentity
) : Command<Game>(aggregateID) {

    override fun execute(currentAggregate: Game?): Result<GameWaitingDealer> = runCatching {
        when (currentAggregate) {
            is GameDraft -> currentAggregate.requestDealCards(requestedBy)
            else -> throw UnsupportedOperationException("Aggregate in a wrong status")
        }
    }
}
