package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Tris
import com.abaddon83.burraco.common.models.TrisIdentity

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

fun Iterable<Tris>.score(): Int =
    this.fold(0){current, tris -> current+tris.score()}

