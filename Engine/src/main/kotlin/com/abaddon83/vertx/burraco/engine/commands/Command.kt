package com.abaddon83.vertx.burraco.engine.commands

import com.abaddon83.vertx.burraco.engine.models.BurracoScale
import com.abaddon83.vertx.burraco.engine.models.BurracoTris
import com.abaddon83.vertx.burraco.engine.models.burracos.BurracoIdentity
import com.abaddon83.vertx.burraco.engine.models.decks.Card
import com.abaddon83.vertx.burraco.engine.models.games.GameIdentity
import com.abaddon83.vertx.burraco.engine.models.players.PlayerIdentity
import java.sql.Timestamp
import java.util.*


interface Command {
    val commandId: UUID
    val commandTimeStamp: Timestamp
}

sealed class CommandImpl(override val commandId: UUID = UUID.randomUUID(), override val commandTimeStamp: Timestamp = Timestamp(System.currentTimeMillis())): Command

data class CreateNewBurracoGameCmd( val gameIdentity: GameIdentity) : CommandImpl()
data class AddPlayerCmd(val gameIdentity: GameIdentity, val playerIdentityToAdd: PlayerIdentity) : CommandImpl()
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

