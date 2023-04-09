package com.story.platform.core.infrastructure.kafka

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
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "1"
                )
            )
        )
    }

    @Bean(DEFAULT_ACK_ALL_KAFKA_TEMPLATE)
    fun ackAllKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all"
                )
            )
        )
    }

    @Bean(POST_KAFKA_TEMPLATE)
    fun postKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all", // 일관성
                    retries = 0, // retries > 1인 경우 순서가 변경될 수 있음.
                    lingerMs = 0, // 지연 없이 발송하기 위함
                )
            )
        )
    }

    @Bean(SUBSCRIPTION_KAFKA_TEMPLATE)
    fun subscriptionKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all", // 일관성
                    retries = 0, // retries > 1인 경우 순서가 변경될 수 있음.
                    lingerMs = 0, // 지연 없이 발송하기 위함
                )
            )
        )
    }

    private fun kafkaConfiguration(
        acksConfig: String,
        maxInflightRequestsPerConnection: Int,
        batchSize: Int = 16384, // 16KB
        bufferMemory: Int = 33_554_432, // 33MB
        lingerMs: Int = 0,
        retries: Int = 5,
        maxRequestSize: Int = 1_048_576, // 1MB
        requestTimeoutMs: Int = 10_000, // 10s
    ): Map<String, Any> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "lz4"
        config[ProducerConfig.ACKS_CONFIG] = acksConfig
        config[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = maxInflightRequestsPerConnection
        config[ProducerConfig.BATCH_SIZE_CONFIG] = batchSize
        config[ProducerConfig.BUFFER_MEMORY_CONFIG] = bufferMemory
        config[ProducerConfig.LINGER_MS_CONFIG] = lingerMs
        config[ProducerConfig.RETRIES_CONFIG] = retries
        config[ProducerConfig.MAX_REQUEST_SIZE_CONFIG] = maxRequestSize
        config[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = requestTimeoutMs
        return config
    }

    companion object {
        const val DEFAULT_KAFKA_TEMPLATE = "defaultAckOneKafkaTemplate"
        const val DEFAULT_ACK_ALL_KAFKA_TEMPLATE = "defaultAckAllKafkaTemplate"
        const val SUBSCRIPTION_KAFKA_TEMPLATE = "subscriptionKafkaTemplate"
        const val POST_KAFKA_TEMPLATE = "postKafkaTemplate"
    }

}
