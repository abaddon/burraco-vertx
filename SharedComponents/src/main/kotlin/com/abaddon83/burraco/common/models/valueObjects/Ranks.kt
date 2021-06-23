package com.abaddon83.burraco.common.models.valueObjects

import com.abaddon83.burraco.common.serializations.RankCustomSerializer
import kotlinx.serialization.Serializable

object Ranks {
    private val noFiguresRanks: List<Rank> = listOf(Ace, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten)
    val fullRanks: List<Rank> = listOf(noFiguresRanks, listOf(Jack, Queen, King)).flatten()

    fun valueOf(value: String): Rank =
        when (val rank = fullRanks.plus(Jolly).find { it.label.equals(value, ignoreCase = true) }) {
            is Rank -> rank
            else -> throw Exception("$value is not a valid Rank")
        }
    @Serializable( with = RankCustomSerializer::class)
    interface Rank {
        val label: String
        val position: Int
    }

    object Ace : Rank {
        override val label: String = "A"
        override val position: Int = 1
    }

    object Two : Rank {
        override val label: String = "2"
        override val position: Int = 2
    }

    object Three : Rank {
        override val label: String = "3"
        override val position: Int = 3
    }

    object Four : Rank {
        override val label: String = "4"
        override val position: Int = 4
    }

    object Five : Rank {
        override val label: String = "5"
        override val position: Int = 5
    }

    object Six : Rank {
        override val label: String = "6"
        override val position: Int = 6
    }

    object Seven : Rank {
        override val label: String = "7"
        override val position: Int = 7
    }

    object Eight : Rank {
        override val label: String = "8"
        override val position: Int = 8
    }

    object Nine : Rank {
        override val label: String = "9"
        override val position: Int = 9
    }

    object Ten : Rank {
        override val label: String = "10"
        override val position: Int = 10
    }

    object Jack : Rank {
        override val label: String = "J"
        override val position: Int = 11
    }

    object Queen : Rank {
        override val label: String = "Q"
        override val position: Int = 12
    }

    object King : Rank {
        override val label: String = "K"
        override val position: Int = 13
    }

    object Jolly : Rank {
        override val label: String = "Jolly"
        override val position: Int = 0
    }
}

