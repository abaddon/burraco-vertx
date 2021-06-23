package com.abaddon83.eventStore.queries

sealed class Query

data class GetEntityEvents(val entityName: String, val identity: String): Query()