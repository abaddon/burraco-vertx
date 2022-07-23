package com.abaddon83.burraco.dealer.models

import com.abaddon83.burraco.common.models.PlayerIdentity

data class Player(
    val id: PlayerIdentity,
    val numCardsDealt: Int
){
    companion object{
        fun create(id: PlayerIdentity): Player =
            Player(id,0)
    }
}
