package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.common.models.identities.PlayerIdentity
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.burraco.readModel.projections.GamePlayer
import com.abaddon83.utils.es.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

class GamePlayerEventHandler(override val repository: RepositoryPort): EventHandlerJob<GamePlayer>() {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)
    }


    override fun processEvent(e: Event) {
        launch {
            log.info("Event ${e.javaClass.simpleName} loaded in the read model")
            val gamePlayer = getProjection(e.key()).applyEvent(e)
            if (gamePlayer.identity.isEmpty()) {
                repository.persist(gamePlayer)
            }
        }
    }

    override fun getProjection(key: String) = repository.findGamePlayer(PlayerIdentity.create(key)!!)




}