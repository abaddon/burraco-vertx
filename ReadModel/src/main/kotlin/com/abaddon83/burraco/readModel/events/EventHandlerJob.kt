package com.abaddon83.burraco.readModel.events

import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.utils.es.Event
import com.abaddon83.utils.es.EventHandler
import io.vertx.core.logging.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class EventHandlerJob<T>() : EventHandler<T>, CoroutineScope {
    protected val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    abstract val repository: RepositoryPort
    abstract val e: Event

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

    init {
        job.invokeOnCompletion {
            log.info("Job ended")
        }
    }

    suspend fun join(){
        job.join()
    }

}