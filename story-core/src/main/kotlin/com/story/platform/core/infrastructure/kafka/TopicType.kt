package com.story.platform.core.infrastructure.kafka

enum class TopicType(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "subscription.event"),
    SUBSCRIPTION_DISTRIBUTOR(description = "구독 분산", property = "subscription.distributor"),
    POST(description = "포스팅", property = "post.event"),
    COMPONENT(description = "컴포넌트", property = "component.event"),
    AUTHENTICATION(description = "인증", property = "authentication.event"),
    PURGE(description = "데이터 삭제", property = "purge.event"),
    ;

}
