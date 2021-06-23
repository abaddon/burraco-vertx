package com.abaddon83.burraco.game.adapters.commandController.models

import com.abaddon83.burraco.game.models.BurracoPlayer
import java.util.*


data class BurracoPlayerModule(
        val id: UUID
) {

    companion object Factory {
        fun from(player: BurracoPlayer): BurracoPlayerModule =
                BurracoPlayerModule(
                        id = player.identity().convertTo()
                )
    }
}