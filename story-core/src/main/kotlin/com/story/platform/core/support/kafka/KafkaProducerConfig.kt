package com.story.platform.core.support.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean(DEFAULT_KAFKA_TEMPLATE)
    fun defaultKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(DefaultKafkaProducerFactory(defaultKafkaConfig()))
    }

    @Bean(ACK_ALL_KAFKA_TEMPLATE)
    fun ackAllKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(DefaultKafkaProducerFactory(ackAllKafkaConfig()))
    }

    private fun defaultKafkaConfig(): Map<String, Any> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "lz4"
        return config
    }

    private fun ackAllKafkaConfig(): Map<String, Any> {
        val config = defaultKafkaConfig() as MutableMap<String, Any>
        config[ProducerConfig.ACKS_CONFIG] = "all"
        return config
    }

    companion object {
        const val DEFAULT_KAFKA_TEMPLATE = "defaultKafkaTemplate"
        const val ACK_ALL_KAFKA_TEMPLATE = "ackAllKafkaTemplate"
    }

}
