package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.common.models.card.Rank

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

    val SCORE: Map<Rank,Int> = mapOf(
        Pair(Rank.Jolly,30),
        Pair(Rank.Two,20),
        Pair(Rank.Ace,15),
        Pair(Rank.King,10),
        Pair(Rank.Queen,10),
        Pair(Rank.Jack,10),
        Pair(Rank.Ten,10),
        Pair(Rank.Nine,10),
        Pair(Rank.Eight,10),
        Pair(Rank.Seven,5),
        Pair(Rank.Six,5),
        Pair(Rank.Five,5),
        Pair(Rank.Four,5),
        Pair(Rank.Three,5)
    )

    fun deckSize(numPlayers: Int): Int = GameConfig.TOTAL_CARDS_REQUIRED
        .minus(GameConfig.DISCARD_DECK_SIZE)
        .minus(GameConfig.NUM_PLAYER_CARDS * numPlayers)
        .minus(GameConfig.FIRST_PLAYER_DECK_SIZE[numPlayers]!!)
        .minus(GameConfig.SECOND_PLAYER_DECK_SIZE)
}