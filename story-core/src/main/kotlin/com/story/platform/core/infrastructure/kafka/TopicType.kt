package com.story.platform.core.infrastructure.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "story.kafka.subscription.topic"),
    POST(description = "포스팅", property = "story.kafka.post.topic"),
    SUBSCRIBER_DISTRIBUTOR(description = "구독자 분산", property = "story.kafka.subscriber.distributor.topic"),
    LOCAL_CACHE_EVICT(description = "로컬 캐시 만료", property = "story.kafka.cache-evict.topic")

}
