package com.abaddon83.burraco.dealer.models

import com.abaddon83.burraco.dealer.models.Rank.*
enum class Rank(val label: String){
    Ace("A"), Two("2"), Three("3"), Four("4"), Five("5"), Six("6"), Seven("7"), Eight("8"), Nine("9"), Ten("10"),Jack("J"), Queen("Q"), King("K"),Jolly("Jolly")
}

object Ranks {
    private val noFiguresRanks: List<Rank> = listOf(Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten)
    val fullRanks: List<Rank> = listOf(noFiguresRanks, listOf(Jack, Queen, King)).flatten()

    fun valueOf(value: String): Rank = Rank.valueOf(value)
}

