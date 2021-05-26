package com.abaddon83.burraco.common.events

import com.abaddon83.utils.ddd.Event
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Scale
import com.abaddon83.burraco.common.models.valueObjects.Tris
import kotlinx.serialization.*
import kotlinx.serialization.json.*


@Serializable
sealed class BurracoGameEvent() : Event() {
    abstract val identity: GameIdentity
    override fun key(): String = identity.convertTo().toString()
    override val entityName: String = "BurracoGame"

    fun toJson(): JsonElement{
        return when(this){
            is BurracoGameCreated -> Json.encodeToJsonElement(this)
            is PlayerAdded -> Json.encodeToJsonElement(this)
            is GameInitialised -> Json.encodeToJsonElement(this)
            else -> throw NotImplementedError("Event not reconised")
        }
    }

    companion object {
        //        inline fun <reified T> jsonToEvent(json: String): T {
//            try{
//                return Json.decodeFromString<T>(json)
//            }catch (e: Exception){
//                throw e
//            }
//        }
        fun jsonToEvent(eventClass: String, json: String): BurracoGameEvent? {
            return when (eventClass) {
                "BurracoGameCreated" -> Json.decodeFromString<BurracoGameCreated>(json)
                "PlayerAdded" -> Json.decodeFromString<PlayerAdded>(json)
                "GameStarted" -> Json.decodeFromString<GameInitialised>(json)

                "CardAssignedToPlayer" -> Json.decodeFromString<CardAssignedToPlayer>(json)
                "CardAssignedToPlayerDeck" -> Json.decodeFromString<CardAssignedToPlayerDeck>(json)
                "CardAssignedToDeck" -> Json.decodeFromString<CardAssignedToDeck>(json)
                "CardAssignedToDiscardDeck" -> Json.decodeFromString<CardAssignedToDiscardDeck>(json)

                "GameReady" -> Json.decodeFromString<GameStarted>(json)

                "CardPickedFromDeck" -> Json.decodeFromString<CardPickedFromDeck>(json)
                "CardsPickedFromDiscardPile" -> Json.decodeFromString<CardsPickedFromDiscardPile>(json)
                else -> throw NotImplementedError("error jsonToEvent(..), ${eventClass.javaClass.simpleName} not managed")
            }
        }
    }
}

@Serializable
data class BurracoGameCreated(override val identity: GameIdentity /*, val deck: List<Card>*/) : BurracoGameEvent()

@Serializable
data class PlayerAdded(override val identity: GameIdentity, val playerIdentity: PlayerIdentity) : BurracoGameEvent()

@Serializable
data class GameInitialised(override val identity: GameIdentity, val players: List<PlayerIdentity> ) : BurracoGameEvent()

@Serializable
data class CardAssignedToPlayer(override val identity: GameIdentity, val player: PlayerIdentity, val card: Card) : BurracoGameEvent()

@Serializable
data class CardAssignedToPlayerDeck(override val identity: GameIdentity, val playerDeckId: Int, val card: Card) : BurracoGameEvent()

@Serializable
data class CardAssignedToDeck(override val identity: GameIdentity, val card: Card) : BurracoGameEvent()

@Serializable
data class CardAssignedToDiscardDeck(override val identity: GameIdentity, val card: Card) : BurracoGameEvent()

@Serializable
data class GameStarted(override val identity: GameIdentity) : BurracoGameEvent()

@Serializable
data class CardPickedFromDeck(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val card: Card) :
    BurracoGameEvent()

@Serializable
data class CardsPickedFromDiscardPile(
    override val identity: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val cards: List<Card>
) : BurracoGameEvent()

@Serializable
data class CardDroppedIntoDiscardPile(
    override val identity: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val card: Card
) : BurracoGameEvent()

@Serializable
data class TrisDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val tris: Tris) :
    BurracoGameEvent()

@Serializable
data class ScaleDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val scale: Scale) :
    BurracoGameEvent()

@Serializable
data class CardAddedOnBurraco(
    override val identity: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val burracoIdentity: BurracoIdentity,
    val cardsToAppend: List<Card>
) : BurracoGameEvent()

@Serializable
data class MazzettoPickedUp(
    override val identity: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val mazzettoDeck: List<Card>
) : BurracoGameEvent()

@Serializable
data class TurnEnded(
    override val identity: GameIdentity,
    val playerIdentity: PlayerIdentity,
    val nextPlayerTurn: PlayerIdentity
) : BurracoGameEvent()

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
