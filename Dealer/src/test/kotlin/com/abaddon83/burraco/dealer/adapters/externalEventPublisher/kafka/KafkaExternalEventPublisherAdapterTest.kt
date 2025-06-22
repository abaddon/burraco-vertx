package com.abaddon83.burraco.dealer.adapters.externalEventPublisher.kafka

import com.abaddon83.burraco.common.adapter.kafka.producer.KafkaProducerConfig
import com.abaddon83.burraco.common.models.DealerIdentity
import com.abaddon83.burraco.common.models.GameIdentity
import com.abaddon83.burraco.common.models.PlayerIdentity
import com.abaddon83.burraco.common.models.card.Rank
import com.abaddon83.burraco.common.models.card.Suit
import com.abaddon83.burraco.dealer.events.CardDealtToDeck
import com.abaddon83.burraco.dealer.events.CardDealtToDiscardDeck
import com.abaddon83.burraco.dealer.events.CardDealtToPlayer
import com.abaddon83.burraco.dealer.events.CardDealtToPlayerDeck1
import com.abaddon83.burraco.dealer.events.CardDealtToPlayerDeck2
import com.abaddon83.burraco.dealer.models.Card
import com.abaddon83.burraco.dealer.models.Dealer
import com.abaddon83.burraco.helper.KafkaContainerTest
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class KafkaExternalEventPublisherAdapterTest : KafkaContainerTest() {
    private val topic: String = "topic"
    private lateinit var kafkaExternalEventPublisherAdapter: KafkaExternalEventPublisherAdapter

    @BeforeEach
    fun setUp() {
        kafkaExternalEventPublisherAdapter = KafkaExternalEventPublisherAdapter(vertx, kafkaProducerConfig())
    }

    @Test
    fun `given a Dealer with a CardDealtToDeck domain event then a CardDealtToDeckExternalEvent is published on kafka`(testContext: VertxTestContext) {
        // Given
        val actualEvents = mutableListOf<String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic) {
            println("Received event: ${it.value()}")
            actualEvents.add(it.value())
        }

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Heart, Rank.Ace)
        val dealer = createMockDealer(dealerIdentity, gameIdentity)
        val event = CardDealtToDeck.create(dealerIdentity, gameIdentity, card)

        // When
        runBlocking {
            kafkaExternalEventPublisherAdapter.publish(dealer, event)
        }

        // Then
        testContext.awaitCompletion(2, TimeUnit.SECONDS)
        assertEquals(expectedEventsCount, actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }
    }

    @Test
    fun `given a Dealer with a CardDealtToPlayer domain event then a CardDealtToPlayerExternalEvent is published on kafka`(testContext: VertxTestContext) {
        // Given
        val actualEvents = mutableListOf<String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic) {
            println("Received event: ${it.value()}")
            actualEvents.add(it.value())
        }

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val playerIdentity = PlayerIdentity.create()
        val card = Card(Suit.Pike, Rank.King)
        val dealer = createMockDealer(dealerIdentity, gameIdentity)
        val event = CardDealtToPlayer.create(dealerIdentity, gameIdentity, playerIdentity, card)

        // When
        runBlocking {
            kafkaExternalEventPublisherAdapter.publish(dealer, event)
        }

        // Then
        testContext.awaitCompletion(2, TimeUnit.SECONDS)
        assertEquals(expectedEventsCount, actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }
    }

    @Test
    fun `given a Dealer with a CardDealtToDiscardDeck domain event then a CardDealtToDiscardDeckExternalEvent is published on kafka`(testContext: VertxTestContext) {
        // Given
        val actualEvents = mutableListOf<String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic) {
            println("Received event: ${it.value()}")
            actualEvents.add(it.value())
        }

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Clover, Rank.Seven)
        val dealer = createMockDealer(dealerIdentity, gameIdentity)
        val event = CardDealtToDiscardDeck.create(dealerIdentity, gameIdentity, card)

        // When
        runBlocking {
            kafkaExternalEventPublisherAdapter.publish(dealer, event)
        }

        // Then
        testContext.awaitCompletion(2, TimeUnit.SECONDS)
        assertEquals(expectedEventsCount, actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }
    }

    @Test
    fun `given a Dealer with a CardDealtToPlayerDeck1 domain event then a CardDealtToPlayerDeck1ExternalEvent is published on kafka`(testContext: VertxTestContext) {
        // Given
        val actualEvents = mutableListOf<String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic) {
            println("Received event: ${it.value()}")
            actualEvents.add(it.value())
        }

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Tile, Rank.Queen)
        val dealer = createMockDealer(dealerIdentity, gameIdentity)
        val event = CardDealtToPlayerDeck1.create(dealerIdentity, gameIdentity, card)

        // When
        runBlocking {
            kafkaExternalEventPublisherAdapter.publish(dealer, event)
        }

        // Then
        testContext.awaitCompletion(2, TimeUnit.SECONDS)
        assertEquals(expectedEventsCount, actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }
    }

    @Test
    fun `given a Dealer with a CardDealtToPlayerDeck2 domain event then a CardDealtToPlayerDeck2ExternalEvent is published on kafka`(testContext: VertxTestContext) {
        // Given
        val actualEvents = mutableListOf<String>()
        val expectedEventsCount = 1
        val consumer = initConsumer(topic) {
            println("Received event: ${it.value()}")
            actualEvents.add(it.value())
        }

        val dealerIdentity = DealerIdentity.create()
        val gameIdentity = GameIdentity.create()
        val card = Card(Suit.Pike, Rank.Ten)
        val dealer = createMockDealer(dealerIdentity, gameIdentity)
        val event = CardDealtToPlayerDeck2.create(dealerIdentity, gameIdentity, card)

        // When
        runBlocking {
            kafkaExternalEventPublisherAdapter.publish(dealer, event)
        }

        // Then
        testContext.awaitCompletion(2, TimeUnit.SECONDS)
        assertEquals(expectedEventsCount, actualEvents.size)
        consumer.close().onComplete { testContext.completeNow() }
    }

    private fun createMockDealer(dealerIdentity: DealerIdentity, gameIdentity: GameIdentity): Dealer {
        return Dealer(
            id = dealerIdentity,
            version = 1,
            gameIdentity = gameIdentity,
            players = listOf(),
            playerDeck1NumCards = 0,
            playerDeck2NumCards = 0,
            discardDeck = 0,
            deckNumCards = 0,
            cardsAvailable = listOf()
        )
    }

    private fun kafkaProducerConfig(): KafkaProducerConfig =
        KafkaProducerConfig.empty()
            .withTopic(topic)
            .withProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers)
            .withProperty(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer"
            )
            .withProperty(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer"
            )
            .withProperty(ProducerConfig.ACKS_CONFIG, "1")

}