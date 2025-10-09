package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.Handler
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.helpers.Validated
import io.github.abaddon.kcqrs.core.helpers.KcqrsLoggerFactory.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class KafkaEventHandler(private val expectedEventName: String) : Handler<KafkaEvent> {

    override fun handle(event: KafkaEvent?) {
        checkNotNull(event)
        check(event.eventName == expectedEventName)
        val job = getCoroutineIOScope().launch {
            val outcome = handleKafkaEventRequest(event)
            logOutcome(outcome)
        }
        runBlocking { job.join() }
    }

    private fun logOutcome(outcome: Validated<*, *>) {
        when (outcome) {
            is Validated.Invalid -> log.error("error on Kafka event execution. {}", outcome.err)
            else -> null
        }
    }

    abstract fun getCoroutineIOScope(): CoroutineScope

    abstract suspend fun handleKafkaEventRequest(event: KafkaEvent): Validated<*, *>

}