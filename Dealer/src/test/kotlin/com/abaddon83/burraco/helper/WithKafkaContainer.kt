package com.abaddon83.burraco.helper

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.*
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.Properties


abstract class WithKafkaContainer {
    companion object {
        @JvmStatic
        var container: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            container.start()
        }

        fun createTopic(names: List<String>, partitions: Int=6){
            val topics=names.map { name ->
                NewTopic(name,partitions,1)
            }
            val result = adminClient().createTopics(topics)
            result.all().get()
        }

        private fun adminClient(): AdminClient =
            AdminClient.create(getAdminProperties())

        private fun getAdminProperties():Properties {
            val properties = Properties()

            properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,container.bootstrapServers)
            return properties
        }

        fun createConsumer(
            bootstrap: String,
            autoOffsetReset: String = "earliest",
            groupId: String = "test",
            autoCommit: Boolean = true,
            autoCommitInterval: Int = 1000,
            keyDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
            valueDeserializer: String = "org.apache.kafka.common.serialization.StringDeserializer",
        ): Consumer<String, String> {
            val props = Properties()
            props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap)
            props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit.toString())
            props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval.toString())
            props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer)
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer)
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset)

            return KafkaConsumer(props)
        }
    }

//    fun Consumer<String, String>.consumeMessages(topic: String, mutableList: MutableList<ConsumerRecord<String, String>>) {
//        subscribe(listOf(topic))
//        while (true) {
//            val messages: ConsumerRecords<String, String> = poll(Duration.ofMillis(5000))
//            if (!messages.isEmpty) {
//                for (message: ConsumerRecord<String, String> in messages) {
//                    mutableList.addAll(messages)
//                    println("Consumer reading message: ${message.value()}")
//                }
//                commitAsync { offsets, exception ->
//                    for ((partition, metadata) in offsets) {
//                        println("Committed offset for topic: ${partition.topic()}, partition: ${partition.partition()}, offset: ${metadata.offset()}")
//                    }
//                }
//            } else {
//                println("No messages to read and poll timeout reached")
//            }
//        }
//    }



}