package com.story.platform.core.infrastructure.kafka

import org.springframework.kafka.core.KafkaTemplate

fun <K : Any, V> KafkaTemplate<K, V>.send(topicType: TopicType, key: K, data: V) =
    this.send(KafkaTopicFinder.getTopicName(topicType), key, data)

fun <K : Any, V> KafkaTemplate<K, V>.send(topicType: TopicType, data: V) =
    this.send(KafkaTopicFinder.getTopicName(topicType), data)
