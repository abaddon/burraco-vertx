package com.abaddon83.burraco.game.models.games

import com.abaddon83.burraco.game.models.decks.Deck
import com.abaddon83.burraco.game.models.players.Player

interface Game{
    val maxPlayers: Int
    val minPlayers: Int
    val numInitialPlayerCards: Int
    val totalCardsRequired: Int
    val players: List<Player>
    val deck: Deck
    fun numPlayers(): Int = players.size
    fun listOfPlayers(): List<Player> = players


}
