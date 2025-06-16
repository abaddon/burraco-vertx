package com.abaddon83.burraco.common.models.card

enum class Rank(val label: String, val position: Int) {
    Ace("A", 1),
    Two("2", 2),
    Three("3", 3),
    Four("4", 4),
    Five("5", 5),
    Six("6", 6),
    Seven("7", 7),
    Eight("8", 8),
    Nine("9", 9),
    Ten("10", 10),
    Jack("J", 11),
    Queen("Q", 12),
    King("K", 13),
    Jolly("J", 0);


    fun getRankComparator() = Comparator<Rank> { r1, r2 ->
        r1.position.compareTo(r2.position)
    }
}

object Ranks {
    private val noFiguresRanks: List<Rank> = listOf(
        Rank.Ace,
        Rank.Two,
        Rank.Three,
        Rank.Four,
        Rank.Five,
        Rank.Six,
        Rank.Seven,
        Rank.Eight,
        Rank.Nine,
        Rank.Ten
    )
    val fullRanks: List<Rank> = listOf(noFiguresRanks, listOf(Rank.Jack, Rank.Queen, Rank.King)).flatten()

    private val labelsMap = fullRanks.associateBy { it.label }

    fun valueOf(label: String): Rank =
        labelsMap[label] ?: throw IllegalArgumentException("No Rank found for label: $label")
}

