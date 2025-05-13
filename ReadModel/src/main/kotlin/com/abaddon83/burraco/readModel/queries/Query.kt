package com.abaddon83.burraco.readModel.queries

sealed class Query

object GetAllBurracoGames: Query()
data class GetBurracoGame(val identity: String): Query()

