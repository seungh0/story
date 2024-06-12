package com.story.core.support.kafka

import com.story.core.common.logger.LoggerExtension.log
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin.NewTopics

@Configuration
class KafkaTopicConfig {

    @Bean
    fun topics(): NewTopics {
        val topics = mutableListOf<NewTopic>()

        for (topicType in KafkaTopic.entries) {
            val topicProperty = KafkaTopicFinder.getTopicProperty(kafkaTopic = topicType)
            topics += TopicBuilder.name(topicProperty.name)
                .replicas(topicProperty.replication)
                .partitions(topicProperty.partitionsCount)
                .build()

            if (topicProperty.enableDLT) {
                topics += TopicBuilder.name(topicProperty.name + "-dlt")
                    .replicas(topicProperty.replication)
                    .partitions(topicProperty.retryPartitionCount)
                    .build()
            }

            (0..<topicProperty.retryTopicsCount - 1).forEach {
                topics += TopicBuilder.name(topicProperty.name + "-retry-$it")
                    .replicas(topicProperty.replication)
                    .partitions(topicProperty.retryPartitionCount)
                    .build()
            }
        }

        log.info { "카프카 토픽이 등록되어 있습니다\n${topics.joinToString(separator = "\n")}" }
        return NewTopics(*topics.toTypedArray())
    }

}
