package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.inMemory

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.ddd.identity.UUIDIdentity


class InMemoryRepositoryAdapter: RepositoryPort {

    private val burracoGameStorage: MutableMap<UUIDIdentity,BurracoGame> = mutableMapOf()
    private val  gamePlayerStorage: MutableMap<String,GamePlayer> = mutableMapOf()

    override fun persist(item: BurracoGame) {
        burracoGameStorage[item.identity] = item
    }

    override fun persist(item: GamePlayer) {
        val key = "${item.identity.id}_${item.gameIdentity.id}"
        gamePlayerStorage[key] = item
    }

    override fun findGame(gameIdentity: GameIdentity): BurracoGame {
        //return burracoGameStorage.getOrDefault(gameIdentity.id,BurracoGame())
        TODO("Not implemented")
    }

    override fun getAllBurracoGame(): List<BurracoGame> {
        return burracoGameStorage.values.toList()
    }

    override fun findGamePlayer(playerIdentity: PlayerIdentity, gameIdentity: GameIdentity): GamePlayer {
        val key = "${playerIdentity.id}_${gameIdentity.id}"
        return gamePlayerStorage.getOrDefault(key,GamePlayer())    }
}