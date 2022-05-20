package com.abaddon83.burraco.dealer.helpers

import com.abaddon83.burraco.dealer.models.Player
import com.abaddon83.burraco.dealer.models.PlayerIdentity


fun Iterable<Player>.contains(playerIdentity: PlayerIdentity): Boolean =
    this.map { player -> player.id }.contains(playerIdentity)

fun Iterable<Player>.playerNumCards(playerIdentity: PlayerIdentity): Int =
    this.find { player -> player.id == playerIdentity }?.numCardsDealt?:-1

fun Iterable<Player>.updatePlayer(
    playerIdentity: PlayerIdentity,
    updateFunction: (player: Player) -> Player
): List<Player> = this
    .map { player ->
        when (player.id) {
            playerIdentity -> updateFunction(player)
            else -> player
        }
    }