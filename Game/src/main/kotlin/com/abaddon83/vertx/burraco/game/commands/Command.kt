package com.abaddon83.vertx.burraco.game.commands

import com.abaddon83.vertx.burraco.game.models.BurracoScale
import com.abaddon83.vertx.burraco.game.models.BurracoTris
import com.abaddon83.burraco.common.models.identities.BurracoIdentity
import com.abaddon83.burraco.common.models.valueObjects.Card
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import java.sql.Timestamp
import java.util.*


interface Command {
    val commandId: UUID
    val commandTimeStamp: Timestamp
}

sealed class CommandImpl(override val commandId: UUID = UUID.randomUUID(), override val commandTimeStamp: Timestamp = Timestamp(System.currentTimeMillis())): Command

data class CreateNewBurracoGameCmd( val gameIdentity: GameIdentity) : CommandImpl()
data class AddPlayerCmd(val gameIdentity: GameIdentity, val playerIdentityToAdd: PlayerIdentity) : CommandImpl()
data class InitGameCmd(val gameIdentity: GameIdentity): CommandImpl()

data class ApplyCardToPlayer(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity, val card: Card): CommandImpl()
data class ApplyCardToPlayerDeck(val gameIdentity: GameIdentity, val playerDeckId: Int, val card: Card): CommandImpl()
data class ApplyCardToDiscardDeck(val gameIdentity: GameIdentity, val card: Card): CommandImpl()
data class ApplyCardToDeck(val gameIdentity: GameIdentity, val card: Card): CommandImpl()

data class StartGameCmd(val gameIdentity: GameIdentity): CommandImpl()

data class PickUpACardFromDeckCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity): CommandImpl()
data class PickUpCardsFromDiscardPileCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity): CommandImpl()
data class DropTrisCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity, val tris: BurracoTris): CommandImpl()
data class DropScaleCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity, val scale: BurracoScale): CommandImpl()
data class PickUpMazzettoDeckCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity ): CommandImpl()
data class AppendCardOnBurracoCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity, val burracoIdentity: BurracoIdentity, val cardsToAppend: List<Card>): CommandImpl()
data class DropCardOnDiscardPileCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity, val card: Card): CommandImpl()
data class EndPlayerTurnCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity): CommandImpl()
data class EndGameCmd(val gameIdentity: GameIdentity, val playerIdentity: PlayerIdentity): CommandImpl()

