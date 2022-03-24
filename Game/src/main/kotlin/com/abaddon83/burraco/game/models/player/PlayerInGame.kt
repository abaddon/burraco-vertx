package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.card.Card

data class PlayerInGame(
    override val id: PlayerIdentity,
    override val cards: List<Card>,
    val listOfTris: List<Tris>,
    val listOfStraight: List<Straight>
) : Player {
    companion object Factory {
        fun from(player: WaitingPlayer): PlayerInGame =
            PlayerInGame(player.id, player.cards, listOf(), listOf())

    }

    fun hasAtLeastABurraco(): Boolean =
        listOfTris.count { it.isBurraco() } + listOfStraight.count { it.isBurraco() } > 0

}