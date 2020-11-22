package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.es.Event
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class GamePlayerEventHandler(override val repository: RepositoryPort, override val e: Event) : EventHandlerJob<GamePlayer>() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }

    init {
        log.info("Loading event ${e.javaClass.simpleName} in GamePlayer projection")
        launch {
            try {
                processEvent(e)
            } catch (e: Exception) {
                throw e
            } finally {
                log.info("Event ${e.javaClass.simpleName} loaded in GamePlayer projection")
                job.cancel()
            }
        }
    }

    override fun processEvent(e: Event) {
        val gamePlayer: GamePlayer = getProjection(e.key())
            .applyEvent(e)
        if (!gamePlayer.identity.isEmpty()) {
            repository.persist(gamePlayer)
        }
    }

    override fun getProjection(key: String): GamePlayer = repository.findGamePlayer(PlayerIdentity.create(key)!!)


}