package com.abaddon83.burraco.player.adapter.commandController.rest.models

import com.abaddon83.burraco.player.projection.playerview.PlayerView

data class PlayerViewModule(
    val playerId: String,
    val gameId: String,
    val cards: List<String>,
    val sequences: List<List<String>>,
    val ranks: List<List<String>>
): Module() {
    companion object {
        fun from(playerView: PlayerView): PlayerViewModule {
            return PlayerViewModule(
                playerId = playerView.key.playerIdentity.valueAsString(),
                gameId = playerView.key.gameIdentity.valueAsString(),
                cards = playerView.cards.map { it.label },
                sequences = playerView.sequences.map { sequence -> sequence.map { it.label } },
                ranks = playerView.ranks.map { rank -> rank.map { it.label } }
            )
        }
    }
}
