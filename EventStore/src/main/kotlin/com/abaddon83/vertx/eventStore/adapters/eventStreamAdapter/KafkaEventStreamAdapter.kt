package com.abaddon83.vertx.eventStore.adapters.eventStreamAdapter

import com.abaddon83.utils.functionals.Invalid
import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import io.vertx.core.Vertx
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import java.util.*

class KafkaEventStreamAdapter(vertx: Vertx): EventStreamPort {

    private val vertx: Vertx = vertx;


    private val config = mutableMapOf(
        ("bootstrap.servers" to "localhost:9092"),
        ("key.serializer" to "org.apache.kafka.common.serialization.StringSerializer"),
        ("value.serializer" to "org.apache.kafka.common.serialization.StringSerializer"),
        ("acks" to "1")
    )

    override fun publish(event: Event): Validated<EventError, OutcomeDetail> {
        return try{
            var producer = KafkaProducer.create<String, String>(vertx, config)
            var record = KafkaProducerRecord.create<String, String>("test", event.jsonPayload)
            producer.write(record)

            Valid(mapOf("eventKey" to record.key(),"eventMsg" to record.value(),"eventTopic" to record.topic()))

        }catch (ex: Exception){
            Invalid(EventError("Event not published, ${ex.cause}"))
        }
    }
}