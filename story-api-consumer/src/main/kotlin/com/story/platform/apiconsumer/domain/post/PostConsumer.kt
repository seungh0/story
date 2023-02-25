package com.story.platform.apiconsumer.domain.post

import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.post.PostEventType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class PostConsumer {

    @KafkaListener(topics = ["\${story.kafka.topic.post}"], groupId = "POST")
    fun handlePostEvent(record: ConsumerRecord<String, String>) {
        val event = JsonUtils.toObject(record.value(), PostEvent::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        when (event.eventType) {
            PostEventType.CREATED -> log.info("Post Created Event: $event")
            PostEventType.UPDATED -> log.info("Post Updated Event: $event")
            PostEventType.DELETED -> log.info("Post Deleted Event: $event")
        }
    }

}
