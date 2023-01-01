package com.story.datacenter.core.support.kafka

import com.story.datacenter.core.support.spring.ApplicationContextProvider

object KafkaTopicFinder {

    private val KAFKA_TOPIC_MAP: HashMap<TopicType, String?> = object : HashMap<TopicType, String?>() {
        init {
            val env = ApplicationContextProvider.applicationContext.environment
        }
    }

    fun getTopicName(topicType: TopicType): String {
        return KAFKA_TOPIC_MAP[topicType]
            ?: throw IllegalArgumentException("등록된 카프카 토픽($topicType)이 존재하지 않습니다")
    }

}
