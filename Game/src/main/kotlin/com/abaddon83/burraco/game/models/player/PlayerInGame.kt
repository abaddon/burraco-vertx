package com.abaddon83.burraco.game.models.player

import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.card.Card

data class PlayerInGame(
    override val id: PlayerIdentity,
    override val cards: List<Card>,
    val listOfTris: List<Tris>
): Player{
    companion object Factory{
        fun from(player: WaitingPlayer): PlayerInGame =
            PlayerInGame(player.id,player.cards, listOf())

    }
}