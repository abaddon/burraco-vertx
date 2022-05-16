package com.abaddon83.burraco.game.helpers

import com.abaddon83.burraco.game.models.Straight
import com.abaddon83.burraco.game.models.StraightIdentity

fun Iterable<Straight>.updateStraight(
    straightIdentity: StraightIdentity,
    updateFunction: (straight: Straight) -> Straight
): List<Straight> = this
    .map { straight ->
        when (straight.id) {
            straightIdentity -> updateFunction(straight)
            else -> straight
        }
    }

fun Iterable<Straight>.score(): Int =
    this.fold(0){current, straight -> current+straight.score()}