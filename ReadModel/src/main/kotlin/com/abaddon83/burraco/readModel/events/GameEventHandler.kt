package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.utils.es.Event
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext


class GameEventHandler(override val repository: RepositoryPort) : EventHandlerJob<BurracoGame>() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    override fun processEvent(e: Event) {
        launch {
            try {
                log.info("Event ${e.javaClass.simpleName} loaded in the read model")
                val burracoGame = getProjection(e.key()).applyEvent(e)
                if (!burracoGame.identity.isEmpty()) {
                    repository.persist(burracoGame)
                }
                //throw Exception()
            }catch (e: Exception){
                throw e
            }finally {
                job.cancel()
            }

        }
    }

    override fun getProjection(key: String) = repository.findGame(GameIdentity.create(key)!!)




}