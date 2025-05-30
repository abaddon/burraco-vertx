package com.abaddon83.burraco.dealer.commands

import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.models.DealerIdentity
import io.github.abaddon.kcqrs.core.domain.messages.commands.Command

data class CreateDecks(
    override val aggregateID: DealerIdentity,
    val gameIdentity: GameIdentity,
    val players: List<PlayerIdentity>
) : Command<Dealer>(aggregateID) {

    override fun execute(currentAggregate: Dealer?): Result<Dealer> = when (currentAggregate) {
        is Dealer -> runCatching {
            currentAggregate.createDeck(aggregateID, gameIdentity, players)
        }

        else -> Result.failure(UnsupportedOperationException("Aggregate in a wrong status"))
    }

}
