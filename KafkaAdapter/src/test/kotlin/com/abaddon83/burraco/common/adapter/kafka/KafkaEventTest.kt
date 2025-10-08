package com.abaddon83.burraco.common.adapter.kafka

import com.abaddon83.burraco.common.externalEvents.ExternalEvent
import com.abaddon83.burraco.common.externalEvents.KafkaEvent
import com.abaddon83.burraco.common.models.GameIdentity
import io.vertx.core.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class KafkaEventTest{

    @Test
    fun `Given a valid KafkaEvent, when execute toJson, then json created`() {
        val gameIdentity = GameIdentity.create()
        val externalEvent = DummyExternalEvent(gameIdentity)
        val kafkaEvent = externalEvent.toKafkaEvent()
        val json = kafkaEvent.toJson()
        assertTrue(json.contains(gameIdentity.valueAsString()))
    }

    @Test
    fun `given a validRecord when converted to KafkaEvent than a valid KafkaGameEvent is created`() {
        val kafkaEventJsonSample="""{"eventName":"CardDealingRequested","eventPayload":"{\"aggregateType\":\"Game\",\"version\":1,\"messageId\":\"a94e244f-57ca-4235-a2d6-a80a40b3df2a\",\"header\":{\"standard\":{\"eventType\":\"Game\",\"createdAt\":\"1658393867\"},\"custom\":{}},\"aggregateId\":{\"identity\":\"e6a89929-fc5d-47a4-b12e-b9ae1c198bb2\"},\"requestedBy\":{\"identity\":\"feae8f2d-4b0c-4331-87e4-60b08300d721\"}}"}"""
        val validRecord: ConsumerRecord<String, String> = ConsumerRecord("topic",0,0,"key",kafkaEventJsonSample)

        val kafkaEvent= KafkaEvent.from(validRecord.value())
        val payload="{\"aggregateType\":\"Game\",\"version\":1,\"messageId\":\"a94e244f-57ca-4235-a2d6-a80a40b3df2a\",\"header\":{\"standard\":{\"eventType\":\"Game\",\"createdAt\":\"1658393867\"},\"custom\":{}},\"aggregateId\":{\"identity\":\"e6a89929-fc5d-47a4-b12e-b9ae1c198bb2\"},\"requestedBy\":{\"identity\":\"feae8f2d-4b0c-4331-87e4-60b08300d721\"}}"
        assertEquals("CardDealingRequested",kafkaEvent.eventName)
        assertEquals(payload,kafkaEvent.eventPayload)
    }

    internal class DummyExternalEvent(
        override val aggregateIdentity: GameIdentity,
    ) : ExternalEvent {
        override val eventOwner: String = "Test"
        override fun toKafkaEvent(): KafkaEvent {
            return KafkaEvent(eventName, Json.encode(this))
        }

        override val eventName: String = this::class.java.simpleName

    }
}