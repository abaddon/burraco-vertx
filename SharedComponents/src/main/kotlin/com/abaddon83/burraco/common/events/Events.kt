package com.abaddon83.burraco.common.events

import com.abaddon83.burraco.common.models.entities.BurracoScale
import com.abaddon83.burraco.common.models.entities.BurracoTris
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.utils.ddd.Event
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement


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
data class TrisDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val tris: BurracoTris) :
    BurracoGameEvent()

@Serializable
data class ScaleDropped(override val identity: GameIdentity, val playerIdentity: PlayerIdentity, val scale: BurracoScale) :
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
