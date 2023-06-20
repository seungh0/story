package com.story.platform.core.domain.post

import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.json.toJson
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PostEventPublisher(
    @Qualifier(KafkaProducerConfig.POST_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,
) {

    suspend fun publishCreatedEvent(
        postSpaceKey: PostSpaceKey,
        postId: Long,
        accountId: String,
        title: String,
        content: String,
        extraJson: String?,
    ) {
        val event = PostEvent.created(
            workspaceId = postSpaceKey.workspaceId,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        eventHistoryManager.withSaveEventHistory(
            workspaceId = postSpaceKey.workspaceId,
            event = event,
        ) {
            kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
        }
    }

    suspend fun publishModifiedEvent(
        postSpaceKey: PostSpaceKey,
        postId: Long,
        accountId: String,
        title: String,
        content: String,
        extraJson: String?,
    ) {
        val event = PostEvent.modified(
            workspaceId = postSpaceKey.workspaceId,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        eventHistoryManager.withSaveEventHistory(
            workspaceId = postSpaceKey.workspaceId,
            event = event,
        ) {
            kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
        }
    }

    suspend fun publishDeletedEvent(
        postSpaceKey: PostSpaceKey,
        postId: Long,
        accountId: String,
    ) {
        val event = PostEvent.deleted(
            workspaceId = postSpaceKey.workspaceId,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
        )
        eventHistoryManager.withSaveEventHistory(
            workspaceId = postSpaceKey.workspaceId,
            event = event,
        ) {
            kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
        }
    }

}
