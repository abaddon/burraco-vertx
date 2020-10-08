package com.abaddon83.vertx.eventStore.commands

sealed class DomainError(val msg: String)

data class EventError(val e: String): DomainError(e)