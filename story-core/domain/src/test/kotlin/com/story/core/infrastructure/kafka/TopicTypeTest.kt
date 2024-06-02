package com.story.core.infrastructure.kafka

import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Assertions.fail

class TopicTypeTest : StringSpec({

    "토픽 프로퍼티는 곂치지 않는다" {
        // given
        val properties = mutableSetOf<String>()

        // when & then
        for (topicType in KafkaTopic.entries) {
            if (properties.contains(topicType.property)) {
                fail<String>("중복되는 토픽($topicType)의 프로퍼티(${topicType.property})가 존재합니다")
            }
            properties += topicType.property
        }
    }

})
