package com.abaddon83.burraco.game.helpers

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

    fun deckSize(numPlayers: Int): Int = GameConfig.TOTAL_CARDS_REQUIRED
        .minus(GameConfig.DISCARD_DECK_SIZE)
        .minus(GameConfig.NUM_PLAYER_CARDS * numPlayers)
        .minus(GameConfig.FIRST_PLAYER_DECK_SIZE[numPlayers]!!)
        .minus(GameConfig.SECOND_PLAYER_DECK_SIZE)
}