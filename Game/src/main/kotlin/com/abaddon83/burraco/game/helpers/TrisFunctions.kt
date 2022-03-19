package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.game.models.TrisIdentity
import com.abaddon83.burraco.game.models.player.Player
import com.abaddon83.burraco.game.models.player.PlayerIdentity

fun Iterable<Tris>.updateTris(
    trisIdentity: TrisIdentity,
    updateFunction: (tris: Tris) -> Tris
): List<Tris> = this
    .map { tris ->
        when (tris.id) {
            trisIdentity -> updateFunction(tris)
            else -> tris
        }
    }