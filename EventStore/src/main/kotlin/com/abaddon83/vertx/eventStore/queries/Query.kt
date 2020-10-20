package com.abaddon83.vertx.eventStore.queries

sealed class Query

data class GetEntityEvents(val entityName: String, val identity: String): Query()