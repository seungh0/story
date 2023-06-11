package com.story.platform.core.domain.post

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
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
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
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
    }

    suspend fun publishDeletedEvent(
        postSpaceKey: PostSpaceKey,
        postId: Long,
        accountId: String,
    ) {
        val event = PostEvent.deleted(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), postId.toString(), event.toJson())
    }

}
