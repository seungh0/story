package com.story.platform.core.support.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean(name = [DEFAULT_CONTAINER_FACTORY])
    fun defaultKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(defaultKafkaConsumerConfig())
        return factory
    }

    fun defaultKafkaConsumerConfig(): Map<String, Any> {
        val config: MutableMap<String, Any> = HashMap()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 500
        config[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = 10_000
        config[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = 3_000
        config[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 300_000
        config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 500
        config[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
        return config
    }

    companion object {
        const val DEFAULT_CONTAINER_FACTORY = "defaultContainerFactory"
    }

}
