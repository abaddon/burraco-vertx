package com.abaddon83.burraco.game.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.VertxCoroutineScope
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerVerticle
import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.game.events.game.GameEvent
import com.abaddon83.burraco.game.models.game.Game
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.helpers.LoggerFactory.log
import kotlinx.coroutines.withContext

class KafkaExternalEventPublisherAdapter(
    vertxCoroutineScope: VertxCoroutineScope,
    kafkaConfig: KafkaProducerConfig
) : KafkaProducerVerticle<GameEvent, Game>(vertxCoroutineScope, kafkaConfig), ExternalEventPublisherPort {

    override fun chooseExternalEvent(aggregate: Game, domainEvent: GameEvent): ExternalEvent? {
        return when (domainEvent) {
            is CardDealingRequested -> {
                val listPlayerIdentities = (aggregate as GameWaitingDealer).players.map { it.id }
                CardsRequestedToDealer(domainEvent.aggregateId, listPlayerIdentities)
            }

            else -> null
        }
    }

    override suspend fun publish(
        aggregate: Game,
        event: GameEvent
    ): Result<Unit> = withContext(vertxCoroutineScope.coroutineContext()) {
        publishOnKafka(aggregate, event)
    }


    override fun onFailure(
        throwable: Throwable,
        aggregate: Game,
        domainEvent: GameEvent,
        extEvent: ExternalEvent
    ): Result<Unit> {
        log.error("Event ${domainEvent.javaClass.simpleName} not published to Kafka", throwable)
        return Result.failure(Exception(throwable))
    }

    override fun onSuccess(aggregate: Game, domainEvent: GameEvent, extEvent: ExternalEvent): Result<Unit> {
        log.info("ExternalEvent  ${extEvent.javaClass.simpleName} published")
        return Result.success(Unit)
    }


}