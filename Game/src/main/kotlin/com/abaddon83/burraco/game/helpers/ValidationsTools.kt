package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity


object ValidationsTools {
    private val UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        .toRegex()

    fun isValidUUID(value: String) = UUID_REGEX.containsMatchIn(value)

    fun playerIsContained(playerIdentity: PlayerIdentity,players: List<Player>): Boolean = players.map { player -> player.id }.contains(playerIdentity)

    fun playerCards(playerIdentity: PlayerIdentity,players: List<Player>): List<Card>? = players
        .find { player -> player.id == playerIdentity }
        ?.cards

    fun updatePlayerInPlayers(players: List<Player>, playerIdentity: PlayerIdentity, updateFunction: (player: Player) -> Player) : List<Player> = players
        .map { player ->
            when (player.id) {
                playerIdentity -> updateFunction(player)//player.copy(cards = player.cards.plus(event.card))
                else -> player
            }
        }

}