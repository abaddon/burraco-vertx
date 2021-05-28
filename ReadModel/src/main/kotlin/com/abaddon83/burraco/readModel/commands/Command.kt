package com.abaddon83.burraco.readModel.commands

import com.abaddon83.burraco.common.events.*
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.utils.ddd.Event
import java.sql.Timestamp
import java.util.*


interface Command {
    val commandId: UUID
    val commandTimeStamp: Timestamp
}

sealed class CommandImpl(override val commandId: UUID = UUID.randomUUID(), override val commandTimeStamp: Timestamp = Timestamp(System.currentTimeMillis())):
    Command


data class ApplyEventBurracoGameCreated( val event: BurracoGameCreated) : CommandImpl()
data class ApplyEventPlayerAdded( val event: PlayerAdded) : CommandImpl()
data class ApplyEventGameInitialised( val event: GameInitialised) : CommandImpl()
data class ApplyEventCardAssignedToPlayer( val event: CardAssignedToPlayer) : CommandImpl()
data class ApplyEventCardAssignedToPlayerDeck( val event: CardAssignedToPlayerDeck) : CommandImpl()
data class ApplyEventCardAssignedToDeck( val event: CardAssignedToDeck) : CommandImpl()
data class ApplyEventCardAssignedToDiscardDeck( val event: CardAssignedToDiscardDeck) : CommandImpl()
data class ApplyEventGameStarted( val event: GameStarted) : CommandImpl()
data class ApplyEventCardPickedFromDeck( val event: CardPickedFromDeck) : CommandImpl()


