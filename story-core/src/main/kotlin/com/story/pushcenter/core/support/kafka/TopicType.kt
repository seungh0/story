package com.story.pushcenter.core.support.kafka

import java.util.*

enum class TopicType(
    private val code: String,
    private val description: String,
) {

    ;

    companion object {
        private val cachedTopicTypes: MutableMap<String, TopicType> = HashMap()

        init {
            for (topicType in TopicType.values()) {
                cachedTopicTypes[topicType.code] = topicType
            }
        }

        fun of(code: String): TopicType {
            return cachedTopicTypes[code.uppercase(Locale.getDefault())]
                ?: throw IllegalArgumentException("등록된 카프카 토픽($code)이 존재하지 않습니다")
        }
    }

}
