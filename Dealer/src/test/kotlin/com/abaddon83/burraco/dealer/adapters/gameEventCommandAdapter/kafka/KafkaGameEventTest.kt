package com.abaddon83.burraco.dealer.adapters.gameEventCommandAdapter.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Test

internal class KafkaGameEventTest{

    val validRecord: ConsumerRecord<String, String> = ConsumerRecord("topic",0,0,"key","value")

    @Test
    fun `given a validRecord when converted to KafkaGameEvent than a valid KafkaGameEvent is created`() {
        val kafkaGameEvent= KafkaGameEvent.createFrom(validRecord)


    }

}