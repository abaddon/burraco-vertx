package com.abaddon83.burraco.common.adapter.kafka.consumer

import com.abaddon83.burraco.common.adapter.kafka.Handler
import com.abaddon83.burraco.common.adapter.kafka.KafkaEvent
import com.abaddon83.burraco.common.helpers.Validated
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class KafkaEventHandler() : Handler<KafkaEvent> {

    override fun handle(event: KafkaEvent?) {
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

    abstract suspend fun handleKafkaEventRequest(event: KafkaEvent?): Validated<*, *>

}