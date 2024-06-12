package com.story.core.support.kafka

import org.springframework.core.annotation.AliasFor
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.retry.annotation.Backoff

@RetryableTopic
@KafkaListener
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class RetryableKafkaListener(
    @get:AliasFor(annotation = KafkaListener::class) val topics: Array<String>,
    @get:AliasFor(annotation = KafkaListener::class) val groupId: String,
    @get:AliasFor(annotation = KafkaListener::class) val containerFactory: String,
    @get:AliasFor(annotation = KafkaListener::class) val concurrency: String = "",
    @get:AliasFor(annotation = RetryableTopic::class) val backoff: Backoff = Backoff(delay = 200, multiplier = 1.5),
    @get:AliasFor(
        annotation = RetryableTopic::class,
        value = "listenerContainerFactory"
    ) val retryTopicListenerContainerFactory: String = KafkaConsumerConfig.DEFAULT_KAFKA_CONSUMER,
    @get:AliasFor(
        annotation = RetryableTopic::class,
        value = "kafkaTemplate"
    ) val retryKafkaTemplate: String = KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER,
    @get:AliasFor(
        annotation = RetryableTopic::class,
        value = "topicSuffixingStrategy"
    ) val retryTopicSuffixingStrategy: TopicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
)
