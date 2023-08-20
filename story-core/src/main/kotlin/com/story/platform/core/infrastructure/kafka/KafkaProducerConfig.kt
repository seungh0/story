package com.story.platform.core.infrastructure.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import java.time.Duration

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean(DEFAULT_KAFKA_PRODUCER)
    fun defaultKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    acksConfig = "1",
                    linger = Duration.ofMillis(0),
                )
            )
        )
    }

    @Bean(DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    fun ackAllKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    acksConfig = "all",
                    linger = Duration.ofMillis(0),
                )
            )
        )
    }

    @Bean(DEFAULT_IDEMPOTENCE_KAFKA_PRODUCER)
    fun idempotenceKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    enableIdempotence = true,
                    maxInflightRequestsPerConnection = 5,
                    acksConfig = "all",
                    retries = Int.MAX_VALUE,
                    linger = Duration.ofMillis(0),
                )
            )
        )
    }

    @Bean(POST_KAFKA_PRODUCER)
    fun postKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    acksConfig = "all",
                    retries = 5,
                    enableIdempotence = true,
                    linger = Duration.ofMillis(200), // 200ms 모아서 배치로 발송
                ),
            )
        )
    }

    @Bean(SUBSCRIPTION_KAFKA_PRODUCER)
    fun subscriptionKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    acksConfig = "all",
                    retries = 5,
                    enableIdempotence = true,
                    linger = Duration.ofMillis(200), // 200ms 모아서 배치로 발송
                )
            )
        )
    }

    @Bean(FEED_KAFKA_PRODUCER)
    fun feedKafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                kafkaConfiguration(
                    acksConfig = "all",
                    retries = 5,
                    enableIdempotence = true,
                    linger = Duration.ofMillis(200), // 200ms 모아서 배치로 발송
                )
            )
        )
    }

    private fun kafkaConfiguration(
        acksConfig: String,
        maxInflightRequestsPerConnection: Int = 5,
        batchSize: Int = 16384, // 16KB
        bufferMemory: Int = 33_554_432, // 33MB
        maxRequestSize: Int = 1_048_576, // 1MB
        linger: Duration = Duration.ofMillis(0),
        retries: Int = Int.MAX_VALUE,
        retryBackOff: Duration = Duration.ofMillis(100),
        enableIdempotence: Boolean = false,
        maxBlock: Duration = Duration.ofSeconds(5),
        requestTimeout: Duration = Duration.ofSeconds(3),
        deliveryTimeout: Duration = Duration.ofSeconds(5),
    ): Map<String, Any> {
        val config: MutableMap<String, Any> = mutableMapOf()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        // snappy, gzip, lz4
        config[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "snappy"

        // acks (all, 1, 0)
        config[ProducerConfig.ACKS_CONFIG] = acksConfig

        // 프로듀서가 서버로부터 응답을 받지 못한 상태에서 전송할 수 있는 최대 메시지의 수
        config[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = maxInflightRequestsPerConnection

        // 프로듀서가 메시지를 전송하기 전에 메세지를 대기시키는 버퍼의 크기 (max.block.ms 동안 블록
        config[ProducerConfig.BUFFER_MEMORY_CONFIG] = bufferMemory

        // 멱등성 프로듀서 기능을 활성화하기 위해서는 max.in.flight.requests.per.connection >= 5 , retires >= 1, acks=all 설정이 필요.
        config[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = enableIdempotence

        // 메시지의 최대 크기 (default: 1MB)
        config[ProducerConfig.MAX_REQUEST_SIZE_CONFIG] = maxRequestSize

        // 배치에 사용될 메모리 양
        config[ProducerConfig.BATCH_SIZE_CONFIG] = batchSize

        // 배치를 전송하기 전 대기하는 시간
        config[ProducerConfig.LINGER_MS_CONFIG] = linger.toMillis()

        // 프로듀서의 전송 버퍼가 가득 차거나, 메타데이터가 아직 사용 가능하지 않을때 블록되는 최대 시간
        config[ProducerConfig.MAX_BLOCK_MS_CONFIG] = maxBlock.toMillis().toInt()

        // max retry
        config[ProducerConfig.RETRIES_CONFIG] = retries

        // retry.backoff.ms
        config[ProducerConfig.RETRY_BACKOFF_MS_CONFIG] = retryBackOff.toMillis().toInt()

        // request timeout (Inflight)
        config[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = requestTimeout.toMillis().toInt()

        // delivery.timeout.ms >= linger.ms + retry.backoff.ms * retires + request.timeout.ms
        config[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = deliveryTimeout.toMillis().toInt()
        return config
    }

    companion object {
        const val DEFAULT_KAFKA_PRODUCER = "defaultAckOneKafkaProducer"
        const val DEFAULT_ACK_ALL_KAFKA_PRODUCER = "defaultAckAllKafkaProducer"
        const val DEFAULT_IDEMPOTENCE_KAFKA_PRODUCER = "defaultIdempotenceKafkaProducer"
        const val SUBSCRIPTION_KAFKA_PRODUCER = "subscriptionKafkaProducer"
        const val POST_KAFKA_PRODUCER = "postKafkaProducer"
        const val FEED_KAFKA_PRODUCER = "feedKafkaProducer"
    }

}
