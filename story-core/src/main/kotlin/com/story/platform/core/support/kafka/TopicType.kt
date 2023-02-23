package com.story.platform.core.support.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "story.kafka.topic.subscription"),
    UNSUBSCRIPTION(description = "구독 취소", property = "story.kafka.topic.unsubscription"),

}
