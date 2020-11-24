package com.abaddon83.burraco.readModel.adapters.eventStreamAdapter

import com.abaddon83.burraco.common.events.BurracoGameEvent
import com.abaddon83.burraco.readModel.adapters.repositoryAdapters.inMemory.InMemoryRepositoryAdapter
import com.abaddon83.burraco.readModel.adapters.repositoryAdapters.mysql.MysqlRepositoryAdapter
import com.abaddon83.burraco.readModel.events.GameEventHandler
import com.abaddon83.burraco.readModel.events.GamePlayerEventHandler
import com.abaddon83.burraco.readModel.ports.RepositoryPort
import com.abaddon83.utils.kafka.models.KafkaEvent
import io.vertx.core.logging.LoggerFactory
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

class KafkaEventStreamVerticle : CoroutineVerticle() {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.qualifiedName)!!

    }

    val repository: RepositoryPort = MysqlRepositoryAdapter()

    private val kafkaProps: () -> Properties = {
        val props = Properties()
        props.put("group.id", "BurracoGameProjection")
        props.put("bootstrap.servers", "kafka1:9092")
        props.put("enable.auto.commit", "false")
        //props.put("auto.commit.interval.ms", 5000)
        props.put("auto.offset.reset", "earliest")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.UUIDDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

        props
    }
    private val topics = hashSetOf("burracogame_event")
    private var consumer: KafkaConsumer<UUID,String>? = null;

    override suspend fun start() {
        //val repository: RepositoryPort = MysqlRepositoryAdapter()

        consumer = KafkaConsumer.create<UUID, String>(vertx, kafkaProps.invoke())
        consumer?.subscribe(topics)

        consumer?.handler { record ->
            try {

                log.info("Processing record key=${record.key()},partition=${record.partition()},offset=${record.offset()}")

                val kafkaEvent = Json.decodeFromString<KafkaEvent>(record.value())
                val event = BurracoGameEvent.jsonToEvent(kafkaEvent.name, kafkaEvent.jsonPayload)
                if (event == null) {
                    log.warn("KafkaEvent key: ${record.key()} - ${kafkaEvent.name} not imported, Json decoding failed")
                    return@handler
                }
                log.info("handlers are starting")
                val gameEventHandler = GameEventHandler(repository, event)
                val gamePlayerEventHandler = GamePlayerEventHandler(repository, event)

                launch {
                    gameEventHandler.join()
                    gamePlayerEventHandler.join()
                }

                consumer!!.commit()

            }catch (ex: Exception){
                ex.printStackTrace()
                log.error(ex.message)
            }

        }
    }

    override suspend fun stop() {
        super.stop()
        consumer?.close(){ar ->
            if(ar.succeeded()){
                log.info("Consumer closed properly");
            }
        }
    }

}