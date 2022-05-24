package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.events.DealerEvent
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

sealed class EventsPortResult<out TException: Exception, out TPublishResult :PublishResult> {
    data class Invalid<TException: Exception>(val err: TException): EventsPortResult<TException, Nothing>()
    data class Valid<TPublishResult: PublishResult>(val value: TPublishResult): EventsPortResult<Nothing, TPublishResult>()
}

data class PublishResult(
    val details: Map<String,String>
)

interface EventsPort {

    suspend fun publish(event: IDomainEvent): EventsPortResult<Exception, PublishResult>

    suspend fun publish(events: List<IDomainEvent>): List<EventsPortResult<Exception, PublishResult>>
}