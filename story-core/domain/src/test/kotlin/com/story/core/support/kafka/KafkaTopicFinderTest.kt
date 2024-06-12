package com.story.core.support.kafka

import com.story.core.IntegrationTest
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec

@IntegrationTest
class KafkaTopicFinderTest : StringSpec({

    "각 카프카 토픽별로 토픽명 프로퍼티를 가져온다" {
        // when & then
        for (topicType in KafkaTopic.entries) {
            val topic = KafkaTopicFinder.getTopicName(topicType)
            if (topic.isBlank()) {
                fail("토픽명 프로퍼티가 입력되지 않은 ($topicType)이 존재합니다")
            }
        }
    }

})
