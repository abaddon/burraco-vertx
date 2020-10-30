package com.abaddon83.burraco.readModel.ports

import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.ddd.UUIDIdentity


interface RepositoryPort {

    fun persist(readEntity: BurracoGame)
    fun findGame(key:UUIDIdentity): BurracoGame
    fun getAllBurracoGame(): List<BurracoGame>

    fun persist(readEntity: GamePlayer)
    fun findGamePlayer(key: UUIDIdentity): GamePlayer
}