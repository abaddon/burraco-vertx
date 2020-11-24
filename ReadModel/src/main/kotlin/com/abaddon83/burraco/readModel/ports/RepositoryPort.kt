package com.abaddon83.burraco.readModel.ports

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.ddd.identity.UUIDIdentity


interface RepositoryPort {

    fun persist(readEntity: BurracoGame)
    fun findGame(gameIdentity: GameIdentity): BurracoGame
    fun getAllBurracoGame(): List<BurracoGame>

    fun persist(readEntity: GamePlayer)
    fun findGamePlayer(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): GamePlayer
}