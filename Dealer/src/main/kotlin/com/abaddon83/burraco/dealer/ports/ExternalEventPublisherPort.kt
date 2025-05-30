package com.abaddon83.burraco.dealer.ports

import com.abaddon83.burraco.dealer.events.DealerEvent
import com.abaddon83.burraco.dealer.models.Dealer
import io.github.abaddon.kcqrs.core.domain.messages.events.IDomainEvent

//sealed class ExternalEventPublisherResult<out TException: Exception, out TPublishResult :PublishResult> {
//    data class Invalid<TException: Exception>(val err: TException): ExternalEventPublisherResult<TException, Nothing>()
//    data class Valid<TPublishResult: PublishResult>(val value: TPublishResult): ExternalEventPublisherResult<Nothing, TPublishResult>()
//}

//data class PublishResult(
//    val details: Map<String,String>
//)

interface ExternalEventPublisherPort {
    suspend fun publish(aggregate: Dealer, event: DealerEvent): Result<Unit> //: ExternalEventPublisherResult<Exception, PublishResult>
}