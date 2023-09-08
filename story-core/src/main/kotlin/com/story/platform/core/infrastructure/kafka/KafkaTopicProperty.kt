package com.story.platform.core.infrastructure.kafka

data class KafkaTopicProperty(
    val name: String,
    val replication: Int,
    val partitionsCount: Int,
    val retryTopicsCount: Int,
    val retryPartitionCount: Int,
    val enableDLT: Boolean,
)
