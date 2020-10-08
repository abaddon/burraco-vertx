package com.abaddon83.vertx.eventStore.ports

import com.abaddon83.vertx.eventStore.commands.CommandHandler
import com.abaddon83.vertx.eventStore.models.Event


interface ControllerPort {

    val repository: RepositoryPort

    val commandHandle: CommandHandler
        get() = CommandHandler(repository)

    fun persist(event:Event): Outcome
}