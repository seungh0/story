package com.story.core.support.kafka

data class KafkaTopicProperty(
    val name: String,
    val replication: Int,
    val partitionsCount: Int,
    val retryTopicsCount: Int,
    val retryPartitionCount: Int,
    val enableDLT: Boolean,
)
