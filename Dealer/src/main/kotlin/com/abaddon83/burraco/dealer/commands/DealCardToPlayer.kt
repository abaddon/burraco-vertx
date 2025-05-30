package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class DealCardToPlayer(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity,
    val player: PlayerIdentity
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Result<Dealer> = when (currentAggregate) {
        is Dealer -> runCatching {
            currentAggregate.dealCardToPlayer(gameIdentity, player)
        }

        else -> Result.failure(UnsupportedOperationException("Aggregate in a wrong status"))
    }

}
