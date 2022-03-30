package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.card.Ranks
import com.abaddon83.burraco.game.models.card.Suits

object GameConfig {
    const val MAX_PLAYERS: Int = 4
    const val MIN_PLAYERS: Int = 2
    const val TOTAL_CARDS_REQUIRED: Int = 108
    const val NUM_PLAYER_CARDS: Int = 11
    const val DISCARD_DECK_SIZE: Int = 1
    val AVAILABLE_PLAYER_DECK_SIZE: Array<Int> = arrayOf(11,13)
    val FIRST_PLAYER_DECK_SIZE: Map<Int,Int> = mapOf(
        Pair(2,AVAILABLE_PLAYER_DECK_SIZE[0]),
        Pair(3,AVAILABLE_PLAYER_DECK_SIZE[1]),
        Pair(4,AVAILABLE_PLAYER_DECK_SIZE[0])
    )
    const val SECOND_PLAYER_DECK_SIZE: Int = 11

    val SCORE: Map<Ranks.Rank,Int> = mapOf(
        Pair(Ranks.Jolly,10),
        Pair(Ranks.Two,20),
        Pair(Ranks.Ace,15),
        Pair(Ranks.King,10),
        Pair(Ranks.Queen,10),
        Pair(Ranks.Jack,10),
        Pair(Ranks.Ten,10),
        Pair(Ranks.Nine,10),
        Pair(Ranks.Eight,10),
        Pair(Ranks.Seven,5),
        Pair(Ranks.Six,5),
        Pair(Ranks.Five,5),
        Pair(Ranks.Four,5),
        Pair(Ranks.Three,5)
    )

    fun deckSize(numPlayers: Int): Int = GameConfig.TOTAL_CARDS_REQUIRED
        .minus(GameConfig.DISCARD_DECK_SIZE)
        .minus(GameConfig.NUM_PLAYER_CARDS * numPlayers)
        .minus(GameConfig.FIRST_PLAYER_DECK_SIZE[numPlayers]!!)
        .minus(GameConfig.SECOND_PLAYER_DECK_SIZE)
}