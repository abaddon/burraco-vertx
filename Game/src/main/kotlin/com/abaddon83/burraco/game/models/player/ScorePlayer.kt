package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.helpers.score
import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.card.Card

data class ScorePlayer private constructor(
    override val id: PlayerIdentity,
    override val cards: List<Card>,
    val listOfTris: List<Tris>,
    val listOfStraight: List<Straight>,
    val score: Int,
) : Player {

    companion object Factory {
        fun from(player: PlayerInGame): ScorePlayer {
            val totalCards = player.listOfTris.map { it.cards }.flatten()
                .plus(player.listOfStraight.map { it.cards }.flatten())
                .plus(player.cards)

            val score = player.listOfTris.score() +
                    player.listOfStraight.score() +
                    totalCards.score()

            return ScorePlayer(
                player.id,
                player.cards,
                player.listOfTris,
                player.listOfStraight,
                score
            )
        }
    }

}