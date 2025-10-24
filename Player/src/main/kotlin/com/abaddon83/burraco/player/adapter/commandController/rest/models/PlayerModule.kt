package com.abaddon83.burraco.player.adapter.commandController.rest.models

import com.abaddon83.burraco.player.model.player.*

data class PlayerModule(
    val playerId: String,
    val gameId: String,
    val user: String,
    val status: String
): Module() {
    companion object {
        fun from(player: Player): PlayerModule {
            return PlayerModule(
                player.id.valueAsString(),
                player.gameIdentity.valueAsString(),
                player.user,
                getStatus(player)
            )
        }

        private fun getStatus(player: Player): String{
            return when(player){
                is PlayerDraft -> "DRAFT"
                is PlayerNotInGame -> "DELETED"
                else -> "UNKNOWN"
            }
        }
    }
}