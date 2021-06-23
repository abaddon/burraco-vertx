package com.abaddon83.burraco.game.models.burracoGameendeds

import com.abaddon83.burraco.game.models.burracoGameExecutions.playerInGames.PlayerInGame
import com.abaddon83.burraco.common.models.entities.Burraco
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.game.models.players.Player

data class PlayerScore private constructor(
        override val identity: PlayerIdentity,
        val winner: Boolean,
        val burracoList: List<BurracoPoint>,
        val remainedCards: List<Card>
): Player() {

    companion object Factory{
        fun create(player: PlayerInGame, winner: Boolean): PlayerScore =
            PlayerScore(
                    identity = player.identity(),
                    winner = winner,
                    burracoList = burracoList(player.burracoList()),
                    remainedCards = player.showMyCards()
            )

        private fun burracoList(burracoList: List<Burraco>): List<BurracoPoint> =
            burracoList.map { burraco ->
                BurracoPoint(burraco)
            }

    }

}