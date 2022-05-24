package com.abaddon83.burraco.dealer.adapters.eventAdapters

import com.abaddon83.burraco.dealer.events.CardDealtToDiscardDeck
import com.abaddon83.burraco.dealer.events.DeckCreated
import com.abaddon83.burraco.dealer.models.*
import com.abaddon83.burraco.testHelpers.WithKafkaContainer
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(VertxExtension::class)
internal class KafkaEventAdapterTest : WithKafkaContainer() {

    private val AGGREGATE_ID = DealerIdentity.create()
    private val GAME_ID = GameIdentity.create()

    @Test
    fun `given an event when published then one event is into Kafka`(testContext: VertxTestContext) {
        val consumer = createConsumer(container.bootstrapServers)
        consumer.subscribe(listOf(topicName))
        //org.apache.logging.log4j.Level.
        val card = Card(Suits.Clover, Ranks.Ace)
        val kafkaEventAdapter = KafkaEventAdapter(vertx, kafkaConfig)
        val events = listOf(
            CardDealtToDiscardDeck.create(AGGREGATE_ID, GAME_ID, card)
        )

        runBlocking {
            kafkaEventAdapter.publish(events)
        }

        runBlocking {
            delay(500L)
            val records=consumer.poll(Duration.ofMillis(1000))
            //records.forEach{ r -> println("Test1: ${r.value()}")}
            consumer.close()
            assertEquals(1,records.count())
            testContext.completeNow()
        }
    }

    @Test
    fun `given 2 events when published then two events are into Kafka`(testContext: VertxTestContext) {
        val consumer = createConsumer(container.bootstrapServers)
        consumer.subscribe(listOf(topicName))
        val card = Card(Suits.Clover, Ranks.Ace)
        val kafkaEventAdapter = KafkaEventAdapter(vertx, kafkaConfig)
        val events = listOf(
            CardDealtToDiscardDeck.create(AGGREGATE_ID, GAME_ID, card),
            DeckCreated.create(AGGREGATE_ID, GAME_ID, listOf(PlayerIdentity.create(), PlayerIdentity.create()),
                listOf(card,card,card)
            )
        )

        runBlocking {
            kafkaEventAdapter.publish(events)
        }

        runBlocking {
            delay(1000L)
            val records=consumer.poll(Duration.ofMillis(2000))
            consumer.close()
            //records.forEach{ r -> println("Test2: ${r.value()}")}
            assertEquals(2,records.count())
            testContext.completeNow()
        }
    }


    companion object {
        private val vertx: Vertx = Vertx.vertx();
        lateinit var kafkaConfig: KafkaProducerConfig
        private const val topicName = "DealerEvents"

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val bootstrapServer = container.bootstrapServers
            createTopic(listOf(topicName))
            kafkaConfig = KafkaProducerConfig(topic = topicName, bootstrapServer = bootstrapServer)

        }
    }
}