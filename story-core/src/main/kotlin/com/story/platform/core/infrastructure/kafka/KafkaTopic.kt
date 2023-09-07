package com.story.platform.core.infrastructure.kafka

enum class KafkaTopic(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "subscription"),
    FEED(description = "구독 분산", property = "feed"),
    POST(description = "포스팅", property = "post"),
    COMPONENT(description = "컴포넌트", property = "component"),
    AUTHENTICATION(description = "인증", property = "authentication"),
    PURGE(description = "데이터 삭제", property = "purge"),
    WORKSPACE(description = "워크스페이스", property = "workspace"),
    REACTION(description = "리액션", property = "reaction"),
    ;

}
