package com.abaddon83.burraco.game.adapters.gameEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.externalEvents.game.CardsRequestedToDealer
import com.abaddon83.burraco.game.events.game.CardDealingRequested
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.game.models.game.GameDraft
import com.abaddon83.burraco.game.models.game.GameWaitingDealer
import com.abaddon83.burraco.game.models.player.WaitingPlayer
import com.abaddon83.burraco.helpers.KafkaContainerTest
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit


internal class KafkaGameEventPublisherAdapterTest() : KafkaContainerTest(){

    val topic: String = "topic"

    private fun kafkaProducerConfig(): KafkaProducerConfig =
        KafkaProducerConfig.empty()
            .withTopic(topic)
            .withProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers)
            .withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
            .withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
            .withProperty(ProducerConfig.ACKS_CONFIG,"1")


    @Test
    fun `test`(testContext: VertxTestContext){
        val actualEvents = mutableListOf <String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic,Handler {
            println(it.value())
            actualEvents.add(it.value())
        })
        //println("${kafka.bootstrapServers}")
        val kafkaGameEventPublisherAdapter=KafkaGameEventPublisherAdapter(vertx, kafkaProducerConfig())
        val gameIdentity = GameIdentity.create()
        val playerIdentity1 = PlayerIdentity.create()
        val playerIdentity2 = PlayerIdentity.create()

        val aggregate = GameWaitingDealer.from(GameDraft(gameIdentity,1, listOf(
            WaitingPlayer(playerIdentity1, listOf()) ,
            WaitingPlayer(playerIdentity2, listOf())
            ))
        )
        val event = CardDealingRequested.create(gameIdentity, playerIdentity1)
        runBlocking {
            kafkaGameEventPublisherAdapter.publish(aggregate,event)
        }

        testContext.awaitCompletion(2,TimeUnit.SECONDS)
        assertEquals(expectedEventsCount,actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }


    }

}