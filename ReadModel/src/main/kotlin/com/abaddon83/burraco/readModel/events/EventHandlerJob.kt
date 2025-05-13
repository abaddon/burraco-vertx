package com.abaddon83.burraco.readModel.events

import com.abaddon83.utils.ddd.Event
import com.abaddon83.utils.ddd.readModel.EventHandler
import com.abaddon83.utils.ddd.readModel.ProjectionKey
import io.vertx.core.logging.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


abstract class EventHandlerJob<T>(private val e: Event) : EventHandler<T>, CoroutineScope {
    protected val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job


    //abstract val e: Event

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

    init {
//        log.info("Received event ${e.javaClass.simpleName} in ${this::class.qualifiedName}")
//        launch {
//            try {
//                val key = getKey(e)
//                if(key != null){
//                    processEvent(key,e)
//                }
//            } catch (e: Exception) {
//                throw e
//            } finally {
//                job.cancel()
//            }
//        }

        job.invokeOnCompletion {
            log.info("Job ended")
        }
    }

    abstract fun getKey(e: Event): ProjectionKey?


    suspend fun join(){
        job.join()
    }

}