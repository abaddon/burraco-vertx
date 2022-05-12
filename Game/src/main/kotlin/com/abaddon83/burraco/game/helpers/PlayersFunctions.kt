package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.StraightIdentity
import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity
import com.abaddon83.burraco.game.models.player.PlayerInGame

fun <TPlayer : Player> Iterable<TPlayer>.contains(playerIdentity: PlayerIdentity): Boolean =
    this.map { player -> player.id }.contains(playerIdentity)

fun <TPlayer : Player> Iterable<TPlayer>.playerCards(playerIdentity: PlayerIdentity): List<Card>? =
    this.find { player -> player.id == playerIdentity }?.cards

fun <TPlayer : Player> Iterable<TPlayer>.updatePlayer(
    playerIdentity: PlayerIdentity,
    updateFunction: (player: TPlayer) -> TPlayer
): List<TPlayer> = this
    .map { player ->
        when (player.id) {
            playerIdentity -> updateFunction(player)
            else -> player
        }
    }

fun <TPlayer : Player> Iterable<TPlayer>.cardsBelongPlayer(playerIdentity: PlayerIdentity, cards: List<Card>): Boolean =
    this.find { it.id == playerIdentity }?.cards.orEmpty().containsAll(cards)

fun <TPlayer : Player> Iterable<TPlayer>.validPlayer(playerIdentity: PlayerIdentity): Boolean =
    this.count { player -> player.id == playerIdentity } == 1

fun Iterable<PlayerInGame>.trisBelongPlayer(playerIdentity: PlayerIdentity, trisIdentity: TrisIdentity): Boolean =
    this.count { it.id == playerIdentity && it.listOfTris.count { tris -> tris.id == trisIdentity } == 1 } == 1

fun Iterable<PlayerInGame>.tris(playerIdentity: PlayerIdentity, trisIdentity: TrisIdentity): Tris? =
    this.find { it.id == playerIdentity }?.listOfTris?.find { it.id == trisIdentity }

fun Iterable<PlayerInGame>.straightBelongPlayer(playerIdentity: PlayerIdentity, straightIdentity: StraightIdentity): Boolean =
    this.count { it.id == playerIdentity && it.listOfStraight.count { straight -> straight.id == straightIdentity } == 1 } == 1

fun Iterable<PlayerInGame>.straight(playerIdentity: PlayerIdentity, straightIdentity: StraightIdentity): Straight? =
    this.find { it.id == playerIdentity }?.listOfStraight?.find { it.id == straightIdentity }

fun Iterable<PlayerInGame>.hasAtLeastABurraco(): Boolean =
    this.map { player -> player.hasAtLeastABurraco() }.contains(true)