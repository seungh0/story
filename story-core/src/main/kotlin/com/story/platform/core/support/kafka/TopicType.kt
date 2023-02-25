package com.story.platform.core.support.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "story.kafka.topic.subscription"),
    POST(description = "포스팅", property = "story.kafka.topic.subscription"),

}
