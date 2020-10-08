package com.abaddon83.vertx.eventStore.commands

import com.abaddon83.vertx.eventStore.models.Event
import java.sql.Timestamp
import java.util.*


interface Command {
    val commandId: UUID
    val commandTimeStamp: Timestamp
}

sealed class CommandImpl(override val commandId: UUID = UUID.randomUUID(), override val commandTimeStamp: Timestamp = Timestamp(System.currentTimeMillis())): Command

data class PersistEventCmd( val event: Event) : CommandImpl()

