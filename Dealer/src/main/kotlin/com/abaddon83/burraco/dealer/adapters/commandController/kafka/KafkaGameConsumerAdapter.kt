package com.abaddon83.burraco.dealer.adapters.commandController.kafka

import com.abaddon83.burraco.common.adapter.kafka.consumer.EventRouterHandler
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerConfig
import com.abaddon83.burraco.common.adapter.kafka.consumer.KafkaConsumerVerticle
import com.abaddon83.burraco.dealer.adapters.commandController.CommandControllerAdapter
import com.abaddon83.burraco.dealer.adapters.commandController.kafka.handlers.CardsRequestedToDealerHandler
import com.abaddon83.burraco.dealer.adapters.externalEventPublisher.DummyExternalEventPublisher
import com.abaddon83.burraco.dealer.commands.AggregateDealerCommandHandler
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.dealer.ports.CommandControllerPort
import com.abaddon83.burraco.dealer.ports.ExternalEventPublisherPort
import io.github.abaddon.kcqrs.core.persistence.IAggregateRepository

class KafkaGameConsumerAdapter(
    kafkaConfig: KafkaConsumerConfig,
    private val commandController: CommandControllerPort
) : KafkaConsumerVerticle(kafkaConfig){

    override fun loadHandlers(): EventRouterHandler = EventRouterHandler()
        .addHandler("CardsRequestedToDealer", CardsRequestedToDealerHandler(commandController))

    companion object{
        fun build(
            kafkaConfig: KafkaConsumerConfig,
            repository: IAggregateRepository<Dealer>,
            dealerEventPublisher: ExternalEventPublisherPort
        ): KafkaGameConsumerAdapter{
            val commandController = CommandControllerAdapter(AggregateDealerCommandHandler(repository, dealerEventPublisher))
            return KafkaGameConsumerAdapter(kafkaConfig,commandController)
        }
    }

}