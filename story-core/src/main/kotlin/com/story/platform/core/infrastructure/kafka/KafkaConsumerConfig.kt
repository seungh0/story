package com.story.platform.core.infrastructure.kafka

import com.story.platform.core.common.logger.LoggerExtension.log
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean(name = [POST_CONTAINER_FACTORY])
    fun postKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [SUBSCRIPTION_CONTAINER_FACTORY])
    fun subscriptionKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [COMPONENT_CONTAINER_FACTORY])
    fun componentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [AUTHENTICATION_KEY_CONTAINER_FACTORY])
    fun authenticationKeyKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    fun concurrentKafkaListenerContainerFactory(
        maxPollRecords: Int = 500,
        enableAutoCommit: Boolean = true,
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(
            defaultKafkaConsumerConfig(
                maxPollRecords = maxPollRecords,
                enableAutoCommit = enableAutoCommit,
            )
        )
        factory.setConcurrency(1)

        factory.setCommonErrorHandler(
            DefaultErrorHandler({ consumerRecord, exception ->
                if (consumerRecord.value() == null) {
                    log.error("[Topic: ${consumerRecord.topic()}] value is null", exception)
                } else {
                    log.error(
                        """
                        [topic: ${consumerRecord.topic()}] consume fail
                        cause: ${exception.message}
                        key: ${consumerRecord.key()}
                        value: ${consumerRecord.value()}
                        headers: ${consumerRecord.headers()}
                        offset: ${consumerRecord.offset()}
                        """.trimIndent(),
                        exception
                    )
                }
            }, FixedBackOff(1000L, 3L))
        )
        return factory
    }

    fun defaultKafkaConsumerConfig(
        maxPollRecords: Int = 500,
        enableAutoCommit: Boolean = true,
    ): Map<String, Any> {
        val config: MutableMap<String, Any> = HashMap()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = kafkaProperties.admin.isAutoCreate
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = maxPollRecords
        config[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = 10_000
        config[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = 3_000
        config[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 300_000
        config[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = enableAutoCommit
        return config
    }

    companion object {
        const val AUTHENTICATION_KEY_CONTAINER_FACTORY = "authenticationKeyContainerFactory"
        const val COMPONENT_CONTAINER_FACTORY = "componentContainerFactory"
        const val POST_CONTAINER_FACTORY = "postContainerFactory"
        const val SUBSCRIPTION_CONTAINER_FACTORY = "subscriptionContainerFactory"
    }

}
