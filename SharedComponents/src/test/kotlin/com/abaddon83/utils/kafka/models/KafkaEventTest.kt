package com.abaddon83.utils.kafka.models

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

internal class KafkaEventTest{

    @Test
    fun `given a KafkaEvent, when I serialise and deserialize it then I should get the same object`() {
       val kafkaEvent = KafkaEvent("name", UUID.randomUUID(),"entityName", Instant.now(),"jsonPayload")

        val jsonString: String = Json.encodeToString(kafkaEvent);
        println("KafkaEvent  json: $jsonString")
        val deserializedKafkaEvent = Json.decodeFromString<KafkaEvent>(jsonString)
        assertEquals(kafkaEvent, deserializedKafkaEvent)

    }

    @Test
    fun `given a 2 KafkaEvents, when I compare them then they are different`() {
        val kafkaEvent = KafkaEvent("name", UUID.randomUUID(),"entityName", Instant.now(),"jsonPayload")
        val kafkaEvent2 = KafkaEvent("name", UUID.randomUUID(),"entityName", Instant.now(),"jsonPayload")

        assertNotEquals(kafkaEvent, kafkaEvent2)

    }
}