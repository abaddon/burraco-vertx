package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.BurracoGameKey
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.ProjectionKey
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class GameEventHandler(val repository: RepositoryPort, event: BurracoGameEvent) : EventHandlerJob<BurracoGame>(event) {
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
        log.info("Loading event ${e.javaClass.simpleName} in BurracoGame projection")

        val burracoGame: BurracoGame = getProjection(projectionKey)
        val updatedBurracoGame= burracoGame.applyEvent(e)

        if(updatedBurracoGame != burracoGame && !updatedBurracoGame.identity.isEmpty()){
                repository.persist(updatedBurracoGame)
        }

    }

    override fun getProjection(projectionKey: ProjectionKey): BurracoGame {
        check(projectionKey is BurracoGameKey){"wrong projectionKey found, expected BurracoGameKey"}
        check(repository != null){"Repository nullo"}
        log.info("KEY: BurracoGameKey: ${projectionKey.gameIdentity}")
        return repository.findGame(projectionKey.gameIdentity)
    }

    override fun getKey(e: Event): BurracoGameKey? {
        return when(e){
            is BurracoGameEvent -> BurracoGameKey(e.identity)
            else -> null
        }
    }


}