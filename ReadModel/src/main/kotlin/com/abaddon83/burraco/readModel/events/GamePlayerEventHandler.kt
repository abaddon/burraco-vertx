package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.events.CardsDealtToPlayer
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.burraco.readModel.projections.GamePlayerKey
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.ProjectionKey
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class GamePlayerEventHandler(val repository: RepositoryPort, event: Event) : EventHandlerJob<GamePlayer>(event) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    init {
        log.info("Received event ${event.javaClass.simpleName} in ${this::class.qualifiedName}")
        launch {
            try {
                val key = getKey(event)
                if(key != null){
                    processEvent(key,event)
                }
            } catch (e: Exception) {
                throw e
            } finally {
                job.cancel()
            }
        }
    }

    override fun processEvent(projectionKey: ProjectionKey, e: Event) {
        log.info("Loading event ${e.javaClass.simpleName} in GamePlayer projection")
        val gamePlayer: GamePlayer = getProjection(projectionKey)  //Create a class Key with other class that extend it
        val updatedGamePlayer = gamePlayer.applyEvent(e)

        if(updatedGamePlayer != gamePlayer && !updatedGamePlayer.identity.isEmpty()){
            repository.persist(updatedGamePlayer)
        }

        if (!gamePlayer.identity.isEmpty()) {
            repository.persist(gamePlayer)
        }
    }

    override fun getProjection(projectionKey: ProjectionKey): GamePlayer {
        check(projectionKey is GamePlayerKey){"wrong projectionKey found, expected GamePlayerKey"}
        check(repository != null){"Repository nullo"}
        log.info("KEY: GamePlayerKey: ${projectionKey.gameIdentity} + ${projectionKey.identity}")
        return repository.findGamePlayer(projectionKey.identity,projectionKey.gameIdentity)
    }

    override fun getKey(e: Event): GamePlayerKey?{
        return when(e){
            is PlayerAdded -> GamePlayerKey(e.playerIdentity,e.identity)
            is CardsDealtToPlayer -> GamePlayerKey(e.player,e.identity)
            else -> null
        }
    }


}