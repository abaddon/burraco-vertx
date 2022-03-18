package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Card
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity


object ValidationsTools {
    private val UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        .toRegex()

    fun validUUID(value: String): Boolean = UUID_REGEX.containsMatchIn(value)

    fun <TPlayer : Player> playersListContains(playerIdentity: PlayerIdentity, players: List<TPlayer>): Boolean =
        players.map { player -> player.id }.contains(playerIdentity)

    fun <TPlayer : Player> playerCards(playerIdentity: PlayerIdentity, players: List<TPlayer>): List<Card>? = players
        .find { player -> player.id == playerIdentity }
        ?.cards

    fun <TPlayer : Player> updatePlayerInPlayers(
        players: List<TPlayer>,
        playerIdentity: PlayerIdentity,
        updateFunction: (player: TPlayer) -> TPlayer
    ): List<TPlayer> = players
        .map { player ->
            when (player.id) {
                playerIdentity -> updateFunction(player)
                else -> player
            }
        }

//    fun updatePlayerInPlayers(players: List<PlayerInGame>, playerIdentity: PlayerIdentity, updateFunction: (player: PlayerInGame) -> PlayerInGame) : List<WaitingPlayer> = players
//        .map { player ->
//            when (player.id) {
//                playerIdentity -> updateFunction(player)
//                else -> player
//            }
//        }

    fun initialDeckSize(numPlayers: Int): Int = GameConfig.TOTAL_CARDS_REQUIRED
        .minus(GameConfig.DISCARD_DECK_SIZE)
        .minus(GameConfig.NUM_PLAYER_CARDS * numPlayers)
        .minus(GameConfig.FIRST_PLAYER_DECK_SIZE[numPlayers]!!)
        .minus(GameConfig.SECOND_PLAYER_DECK_SIZE)

    fun <TPlayer : Player> cardsBelongPlayer(
        players: List<TPlayer>,
        playerIdentity: PlayerIdentity,
        cards: List<Card>
    ): Boolean =
        players.find { it.id == playerIdentity }?.cards.orEmpty().containsAll(cards)

    fun <TPlayer : Player> validPlayer(players: List<TPlayer>, playerIdentity: PlayerIdentity): Boolean =
        players.count { player -> player.id == playerIdentity } == 1

}