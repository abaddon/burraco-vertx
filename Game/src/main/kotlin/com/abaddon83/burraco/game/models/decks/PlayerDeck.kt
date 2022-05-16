package com.abaddon83.burraco.game.models.decks

import com.abaddon83.burraco.game.helpers.GameConfig.AVAILABLE_PLAYER_DECK_SIZE
import com.abaddon83.burraco.game.models.card.Card

data class PlayerDeck constructor(override val cards: List<Card>) : IDeck {

    companion object Factory {
        fun create(cards: List<Card>): PlayerDeck {
            require(AVAILABLE_PLAYER_DECK_SIZE.contains(cards.size)) { "PlayerDeck Size is wrong, current size: ${cards.size}" }
            return PlayerDeck(cards)
        }

    }

}