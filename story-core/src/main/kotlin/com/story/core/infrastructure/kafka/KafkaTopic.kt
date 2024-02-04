package com.story.core.infrastructure.kafka

enum class KafkaTopic(
    private val description: String,
    val property: String,
) {

    SUBSCRIPTION(description = "구독", property = "subscription"),
    FEED_FANOUT(description = "구독 Fanout", property = "feed-fanout"),
    FEED_MAPPING(description = "구독 매핑", property = "feed-mapping"),
    POST(description = "포스팅", property = "post"),
    COMPONENT(description = "컴포넌트", property = "component"),
    API_KRY(description = "인증", property = "api-key"),
    PURGE(description = "데이터 삭제", property = "purge"),
    WORKSPACE(description = "워크스페이스", property = "workspace"),
    REACTION(description = "리액션", property = "reaction"),
    ;

}
