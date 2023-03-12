package com.story.platform.core.infrastructure.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "story.kafka.subscription.topic"),
    POST(description = "포스팅", property = "story.kafka.post.topic"),

}
