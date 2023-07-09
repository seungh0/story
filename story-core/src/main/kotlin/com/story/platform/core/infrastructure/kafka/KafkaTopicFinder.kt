package com.story.platform.core.infrastructure.kafka

import com.story.platform.core.common.error.NotSupportedException
import com.story.platform.core.common.spring.ApplicationContextProvider

object KafkaTopicFinder {

    private val KAFKA_TOPIC_MAP: HashMap<TopicType, String?> = object : HashMap<TopicType, String?>() {
        init {
            val env = ApplicationContextProvider.applicationContext.environment
            TopicType.values().forEach { topicType ->
                put(topicType, env.getProperty(topicType.property))
            }
        }
    }

    fun getTopicName(topicType: TopicType): String {
        return KAFKA_TOPIC_MAP[topicType]
            ?: throw NotSupportedException("등록된 카프카 토픽($topicType)이 존재하지 않습니다")
    }

}
