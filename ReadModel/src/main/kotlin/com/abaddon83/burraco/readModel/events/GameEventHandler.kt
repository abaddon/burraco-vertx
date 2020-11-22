package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.utils.es.Event
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class GameEventHandler(override val repository: RepositoryPort, override val e: BurracoGameEvent) : EventHandlerJob<BurracoGame>() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    init {
        log.info("Loading event ${e.javaClass.simpleName} in the Game projection")
        launch {
            try {
                processEvent(e)
            }catch (e: Exception){
                throw e
            }finally {
                log.info("Event ${e.javaClass.simpleName} loaded in Game projection")
                job.cancel()
            }
        }
    }

    override fun processEvent(e: Event) {
        val burracoGame: BurracoGame = getProjection(e.key())
            .applyEvent(e)
        if (!burracoGame.identity.isEmpty()) {
            repository.persist(burracoGame)
        }
    }

    override fun getProjection(key: String): BurracoGame = repository.findGame(GameIdentity.create(key)!!)




}