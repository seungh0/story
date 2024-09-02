package com.story.core.support.kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerPartitionPausingBackOffManager
import org.springframework.kafka.listener.ContainerPausingBackOffHandler
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaConsumerBackoffManager
import org.springframework.kafka.listener.ListenerContainerPauseService
import org.springframework.kafka.listener.ListenerContainerRegistry
import org.springframework.kafka.listener.MessageListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.Duration

@Configuration
class DelayedKafkaConsumerConfig {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<Any, Any>,
        registry: ListenerContainerRegistry,
        scheduler: TaskScheduler,
    ): ConcurrentKafkaListenerContainerFactory<Any, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        factory.consumerFactory = consumerFactory
        val backOffManager: KafkaConsumerBackoffManager = createBackOffManager(registry, scheduler)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        factory.setContainerCustomizer { container: ConcurrentMessageListenerContainer<Any, Any> ->
            val delayedAdapter = wrapWithDelayedMessageListenerAdapter(backOffManager, container)
            delayedAdapter.setDelayForTopic("web.orders", Duration.ofSeconds(10))
            delayedAdapter.setDefaultDelay(Duration.ZERO)
            container.setupMessageListener(delayedAdapter)
        }
        return factory
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(JavaTimeModule())
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        return ThreadPoolTaskScheduler()
    }

    private fun wrapWithDelayedMessageListenerAdapter(
        backOffManager: KafkaConsumerBackoffManager,
        container: ConcurrentMessageListenerContainer<Any, Any>,
    ): DelayedMessageListenerAdapter<out Any, out Any> {
        return DelayedMessageListenerAdapter(
            container.containerProperties
                .messageListener as MessageListener<*, *>,
            backOffManager, container.listenerId
        )
    }

    private fun createBackOffManager(
        registry: ListenerContainerRegistry,
        scheduler: TaskScheduler,
    ): ContainerPartitionPausingBackOffManager {
        return ContainerPartitionPausingBackOffManager(
            registry,
            ContainerPausingBackOffHandler(ListenerContainerPauseService(registry, scheduler))
        )
    }

}
