package com.abaddon83.vertx.eventStore.adapters.eventStreamAdapter


import com.abaddon83.utils.functionals.Valid
import com.abaddon83.utils.functionals.Validated
import com.abaddon83.utils.kafka.models.KafkaEvent
import com.abaddon83.vertx.eventStore.commands.EventError
import com.abaddon83.vertx.eventStore.models.Event
import com.abaddon83.vertx.eventStore.ports.EventStreamPort
import com.abaddon83.vertx.eventStore.ports.OutcomeDetail
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class KafkaEventStreamAdapter(vertx: Vertx) : EventStreamPort {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!
    }

    private val vertx: Vertx = vertx;
    private val TOPIC_NAME: String = "burracogame_event"


    private val kafkaProps: () -> Properties = {
        val props = Properties()
        props.put("bootstrap.servers", "kafka1:9092")
        props.put("key.serializer", "org.apache.kafka.common.serialization.UUIDSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("acks", "1")
        props
    }

    override fun publish(event: Event): Validated<EventError, OutcomeDetail> {

            val kafkaEvent = KafkaEvent(
                name = event.name,
                entityKey = event.entityKey,
                entityName =event.entityName ,
                instant = event.instant ,
                jsonPayload = event.jsonPayload)

            var producer = KafkaProducer.create<UUID, String>(vertx, kafkaProps.invoke())
            var record = KafkaProducerRecord.create<UUID, String>(TOPIC_NAME,event.entityKey, Json.encodeToString(kafkaEvent))
            producer
                .write(record){ar ->
                    if(ar.succeeded()){
                        log.info("Record with key ${record.key()} published!")
                    }else{
                        log.error("Record with key ${record.key()} not published")
                        log.error(ar.cause())
                    }
                    producer.close()
                }
        return Valid(mapOf("key" to record.key().toString()))
    }
}
