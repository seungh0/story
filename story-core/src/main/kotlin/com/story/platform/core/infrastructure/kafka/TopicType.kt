package com.story.platform.core.infrastructure.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "story.kafka.subscription.topic"),
    POST(description = "포스팅", property = "story.kafka.post.topic"),
    SUBSCRIBER_DISTRIBUTOR(description = "구독자 분산", property = "story.kafka.subscriber.distributor.topic"),
    COMPONENT(description = "컴포넌트", property = "story.kafka.component.topic"),
    AUTHENTICATION_KEY(description = "인증 키", property = "story.kafka.authentication-key.topic"),
    PURGE(description = "데이터 삭제", property = "story.kafka.purge.topic"),
    ;

}
