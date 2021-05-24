package com.abaddon83.vertx.burraco.dealer.commands

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import java.sql.Timestamp
import java.util.*


interface Command {
    val commandId: UUID
    val commandTimeStamp: Timestamp
}

sealed class CommandImpl(override val commandId: UUID = UUID.randomUUID(), override val commandTimeStamp: Timestamp = Timestamp(System.currentTimeMillis())): Command

data class DealCards( val gameIdentity: GameIdentity, val players: List<PlayerIdentity>) : CommandImpl()

