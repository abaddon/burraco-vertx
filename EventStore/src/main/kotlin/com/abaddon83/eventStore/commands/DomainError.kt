package com.abaddon83.eventStore.commands

sealed class DomainError(val msg: String)

data class EventError(val e: String): DomainError(e)