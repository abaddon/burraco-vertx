package com.abaddon83.burraco.common.events

import com.abaddon83.utils.es.Event
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Tris
import kotlinx.serialization.*
import kotlinx.serialization.json.Json


@Serializable
sealed class BurracoGameEvent(): Event() {
    abstract val identity: GameIdentity
    override fun key(): String = identity.convertTo().toString()
    override val entityName: String = "BurracoGame"

    companion object{
        fun jsonToEvent(eventClass: String, json: String): BurracoGameEvent?{
            return when(eventClass){
                "BurracoGameCreated" -> Json.decodeFromString<BurracoGameCreated>(json)
                "PlayerAdded" -> Json.decodeFromString<PlayerAdded>(json)
                "GameStarted" -> Json.decodeFromString<GameStarted>(json)
                "CardPickedFromDeck" -> Json.decodeFromString<CardPickedFromDeck>(json)
                "CardsPickedFromDiscardPile" -> Json.decodeFromString<CardsPickedFromDiscardPile>(json)
                else -> null
            }
        }
    }

}

@Serializable
data class BurracoGameCreated(override val identity: GameIdentity, val deck: List<Card>): BurracoGameEvent()
@Serializable
data class PlayerAdded(override val identity: GameIdentity, val playerIdentity: PlayerIdentity) : BurracoGameEvent()
@Serializable
data class GameStarted(
    override val identity: GameIdentity,
    val deck: List<Card>,
    val players: Map<PlayerIdentity,List<Card>>,
    val mazzettoDeck1: List<Card>,
    val mazzettoDeck2: List<Card>,
    val discardPileCards: List<Card>,
    val playerTurn: PlayerIdentity)  : BurracoGameEvent()
@Serializable
data class CardPickedFromDeck(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val card: Card) : BurracoGameEvent()
@Serializable
data class CardsPickedFromDiscardPile(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val cards: List<Card>) : BurracoGameEvent()
@Serializable
data class CardDroppedIntoDiscardPile(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val card: Card) : BurracoGameEvent()
@Serializable
data class TrisDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val tris: Tris) : BurracoGameEvent()
@Serializable
data class ScaleDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val scale: Scale) : BurracoGameEvent()
@Serializable
data class CardAddedOnBurraco(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val burracoIdentity: BurracoIdentity, val cardsToAppend: List<Card>): BurracoGameEvent()
@Serializable
data class MazzettoPickedUp(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val mazzettoDeck: List<Card>): BurracoGameEvent()
@Serializable
data class TurnEnded(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val nextPlayerTurn: PlayerIdentity): BurracoGameEvent()



/*object EventsAdapters  {
    fun fillEventPlayerAddedEvent(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity): PlayerAdded =
                    PlayerAdded(identity = gameIdentity.convertTo().toString(), playerIdentity = playerIdentity.convertTo().toString())

    fun fillEventGameStartedEvent(gameIdentity: GameIdentity, deck: BurracoDeck, players:  Map<PlayerIdentity, List<Card>>, mazzettoDeck1 : MazzettoDeck, mazzettoDeck2 : MazzettoDeck, discardPile: DiscardPile, playerTurn: PlayerIdentity): GameStarted {
        return GameStarted(
                identity = gameIdentity.convertTo().toString(),
                deck = deck.cards.map { card -> card.label },
                players = players.map { playerCards ->
                        mapOf(Pair(playerCards.key.convertTo().toString(), playerCards.value.map { card -> card.label }))
                    }.reduce{ acc, next -> acc + next },
                mazzettoDeck1 = mazzettoDeck1.cards.map { card -> card.label },
                mazzettoDeck2 = mazzettoDeck2.cards.map { card -> card.label },
                discardPileCards = discardPile.cards.map { card -> card.label },
                playerTurn = playerTurn.convertTo().toString()
        )
    }

    fun fillEventCardPickedFromDeck(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): CardPickedFromDeck{
        return CardPickedFromDeck(
                identity = gameIdentity.convertTo().toString(),
                player = playerIdentity.convertTo().toString(),
                card = card.label
        )
    }

    fun fillEventCardsPickedFromDiscardPile(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, cards: List<Card>): CardsPickedFromDiscardPile{
        return CardsPickedFromDiscardPile(
                identity = gameIdentity.convertTo().toString(),
                player = playerIdentity.convertTo().toString(),
                cards = cards.map { card -> card.label }
        )
    }

    fun fillEventCardDroppedIntoDiscardPile(gameIdentity: GameIdentity, playerIdentity: PlayerIdentity, card: Card): CardDroppedIntoDiscardPile {
        return CardDroppedIntoDiscardPile(
                identity = gameIdentity.convertTo().toString(),
                player = playerIdentity.convertTo().toString(),
                card = card.label
        )
    }
}*/
