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

    @Bean(DEFAULT_IDEMPOTENCE_KAFKA_TEMPLATE)
    fun idempotenceKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    enableIdempotence = true,
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all",
                    retries = 5,
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
                    acksConfig = "all",
                    retries = 0, // retries > 1인 경우 순서가 변경될 수 있음.
                    lingerMs = 50, // 50ms 모아서 배치로 발송
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
                    acksConfig = "all",
                    retries = 0, // retries > 1인 경우 순서가 변경될 수 있음.
                    lingerMs = 50, // 50ms 모아서 배치로 발송
                )
            )
        )
    }

    @Bean(FEED_KAFKA_TEMPLATE)
    fun feedKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all",
                    retries = 0, // retries > 1인 경우 순서가 변경될 수 있음.
                    lingerMs = 50, // 50ms 모아서 배치로 발송
                )
            )
        )
    }

    private fun kafkaConfiguration(
        acksConfig: String,
        maxInflightRequestsPerConnection: Int,
        batchSize: Int = 16384, // 16KB
        bufferMemory: Int = 33_554_432, // 33MB
        maxRequestSize: Int = 1_048_576, // 1MB
        lingerMs: Int = 0,
        retries: Int = 5,
        retryBackOffMs: Int = 100,
        enableIdempotence: Boolean = false,
        maxBlockMs: Int = 5_000, // 5s
        requestTimeoutMs: Int = 3_000, // 3s
        deliveryTimeoutMs: Int = 4_000, // 5s
    ): Map<String, Any> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "snappy" // snappy, gzip, lz4
        config[ProducerConfig.ACKS_CONFIG] = acksConfig // acks (all, 1, 0)
        config[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = maxInflightRequestsPerConnection
        config[ProducerConfig.BUFFER_MEMORY_CONFIG] = bufferMemory // 프로듀서가 메시지를 전송하기 전에 메세지를 대기시키는 버퍼의 크기 (max.block.ms 동안 블록

        // batch
        config[ProducerConfig.BATCH_SIZE_CONFIG] = batchSize // 배치에 사용될 메모리 양
        config[ProducerConfig.LINGER_MS_CONFIG] = lingerMs // 배치를 전송하기 전 대기하는 시간

        config[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = enableIdempotence // 멱등성 프로듀서 기능을 활성화하기 위해서는 max.in.flight.requests.per.connection >= 5 , retires >= 1, acks=all 설정이 필요.

        config[ProducerConfig.MAX_REQUEST_SIZE_CONFIG] = maxRequestSize // 메시지의 최대 크기 (default: 1MB)
        config[ProducerConfig.RETRIES_CONFIG] = retries // max retry
        config[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = requestTimeoutMs // request timeout (Inflight)
        config[ProducerConfig.MAX_BLOCK_MS_CONFIG] = maxBlockMs // 프로듀서의 전송 버퍼가 가득 차거나, 메타데이터가 아직 사용 가능하지 않을때 블록되는 최대 시간
        config[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = retryBackOffMs // retry.backoff.ms
        config[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = deliveryTimeoutMs // delivery.timeout.ms >= linger.ms + retry.backoff.ms * retires + request.timeout.ms
        return config
    }

    companion object {
        const val DEFAULT_KAFKA_TEMPLATE = "defaultAckOneKafkaTemplate"
        const val DEFAULT_ACK_ALL_KAFKA_TEMPLATE = "defaultAckAllKafkaTemplate"
        const val DEFAULT_IDEMPOTENCE_KAFKA_TEMPLATE = "defaultIdempotenceKafkaTemplateKafkaTemplate"
        const val SUBSCRIPTION_KAFKA_TEMPLATE = "subscriptionKafkaTemplate"
        const val POST_KAFKA_TEMPLATE = "postKafkaTemplate"
        const val FEED_KAFKA_TEMPLATE = "feedKafkaTemplate"
    }

}
