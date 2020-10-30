package com.abaddon83.burraco.readModel.adapters.repositoryAdapters.inMemory

import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.ddd.UUIDIdentity


class InMemoryRepositoryAdapter: RepositoryPort {

    private val burracoGameStorage: MutableMap<UUIDIdentity,BurracoGame> = mutableMapOf()
    private val  gamePlayerStorage: MutableMap<UUIDIdentity,GamePlayer> = mutableMapOf()

    override fun persist(item: BurracoGame) {
        burracoGameStorage[item.identity] = item
    }

    override fun persist(item: GamePlayer) {
        gamePlayerStorage[item.identity] = item
    }

    override fun findGame(key: UUIDIdentity): BurracoGame {
        return burracoGameStorage.getOrDefault(key,BurracoGame())
    }

    override fun getAllBurracoGame(): List<BurracoGame> {
        return burracoGameStorage.values.toList()
    }

    override fun findGamePlayer(key: UUIDIdentity): GamePlayer {
        return gamePlayerStorage.getOrDefault(key,GamePlayer())
    }
}