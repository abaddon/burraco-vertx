package com.abaddon83.burraco.dealer.models

data class Player(
    val id: PlayerIdentity,
    val numCardsDealt: Int
){
    companion object{
        fun create(id: PlayerIdentity): Player =
            Player(id,0)
    }
}
