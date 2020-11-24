package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.events.BurracoGameCreated
import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.common.events.GameStarted
import com.abaddon83.burraco.common.events.PlayerAdded
import com.abaddon83.burraco.common.models.identities.GameIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.BurracoGame
import com.abaddon83.burraco.readModel.projections.BurracoGameKey
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.burraco.readModel.projections.GamePlayerKey
import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.ProjectionKey
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class GameEventHandler(override val repository: RepositoryPort, event: BurracoGameEvent) : EventHandlerJob<BurracoGame>(event) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

//    init {
//        log.info("Loading event ${e.javaClass.simpleName} in the Game projection")
//        launch {
//            try {
//                processEvent(e)
//            }catch (e: Exception){
//                throw e
//            }finally {
//                log.info("Event ${e.javaClass.simpleName} loaded in Game projection")
//                job.cancel()
//            }
//        }
//    }

    override fun processEvent(projectionKey: ProjectionKey, e: Event) {
        log.info("Loading event ${e.javaClass.simpleName} in BurracoGame projection")
        val burracoGame: BurracoGame = getProjection(projectionKey)
            .applyEvent(e)
        if (!burracoGame.identity.isEmpty()) {
            repository.persist(burracoGame)
        }
    }

    override fun getProjection(projectionKey: ProjectionKey): BurracoGame {
        check(projectionKey is BurracoGameKey)

        return repository.findGame(projectionKey.gameIdentity)
    }

    override fun getKey(e: Event): BurracoGameKey? {
        return when(e){
            is BurracoGameEvent -> BurracoGameKey(e.identity)
            else -> null
        }
    }


}