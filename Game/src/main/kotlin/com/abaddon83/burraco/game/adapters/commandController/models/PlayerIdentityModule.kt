package com.abaddon83.burraco.game.adapters.commandController.models

import com.abaddon83.burraco.common.models.identities.PlayerIdentity

data class PlayerIdentityModule(val code: String, val type: String) {


    companion object Factory {
        fun from(playerIdentity: PlayerIdentity): PlayerIdentityModule =
                PlayerIdentityModule(
                        code = playerIdentity.convertTo().toString(),
                        type = playerIdentity.convertTo().javaClass.simpleName
                )
    }
}