package com.story.core.infrastructure.kafka

import com.story.core.common.error.NotSupportedException
import com.story.core.infrastructure.spring.ApplicationContextProvider

object KafkaTopicFinder {

    private val KAFKA_TOPIC_NAME_MAP: HashMap<KafkaTopic, KafkaTopicProperty?> = object :
        HashMap<KafkaTopic, KafkaTopicProperty?>() {
        init {
            val env = ApplicationContextProvider.applicationContext.environment

            val defaultReplicationCount = env.getProperty("story.kafka.default.replication-count")?.toIntOrNull()
                ?: throw IllegalStateException("기본 레플리케이션 수 설정(story.kafka.default.replicationCount)이 누락되어 있습니다")

            KafkaTopic.values().forEach { topicType ->
                val name = env.getProperty("story.kafka.topic.${topicType.property}.name")
                    ?: throw IllegalStateException("카프카 토픽($topicType) 이름 설정(story.kafka.topic.${topicType.property}.name)이 누락되어 있습니다")

                val replicasCount = env.getProperty("story.kafka.topic.${topicType.property}.replication-count")
                    ?.toIntOrNull()
                    ?: defaultReplicationCount

                val partitionCount = env.getProperty("story.kafka.topic.${topicType.property}.partition-count")
                    ?.toIntOrNull()
                    ?: throw IllegalStateException("카프카 토픽($topicType) 파티션 수 설정(story.kafka.topic.${topicType.property}.partition-count)이 누락되어 있습니다")

                val dltEnabled = env.getProperty("story.kafka.topic.${topicType.property}.dlt.enabled")
                    ?.toBooleanStrictOrNull() ?: false

                val retryAttempts = env.getProperty("story.kafka.topic.${topicType.property}.retry.attempts")
                    ?.toIntOrNull() ?: 0

                val retryPartitionCount = env.getProperty("story.kafka.topic.${topicType.property}.retry.partition-count")
                    ?.toIntOrNull() ?: partitionCount

                put(
                    topicType,
                    KafkaTopicProperty(
                        name = name,
                        replication = replicasCount,
                        partitionsCount = partitionCount,
                        enableDLT = dltEnabled,
                        retryTopicsCount = retryAttempts,
                        retryPartitionCount = retryPartitionCount
                    )
                )
            }
        }
    }

    fun getTopicName(kafkaTopic: KafkaTopic): String {
        return KAFKA_TOPIC_NAME_MAP[kafkaTopic]?.name
            ?: throw NotSupportedException("등록된 카프카 토픽($kafkaTopic)이 존재하지 않습니다")
    }

    fun getTopicProperty(kafkaTopic: KafkaTopic): KafkaTopicProperty {
        return KAFKA_TOPIC_NAME_MAP[kafkaTopic]
            ?: throw NotSupportedException("등록된 카프카 토픽($kafkaTopic)이 존재하지 않습니다")
    }

}
