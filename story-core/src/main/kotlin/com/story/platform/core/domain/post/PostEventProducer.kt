package com.story.platform.core.domain.post

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaRecordKeyGenerator
import com.story.platform.core.infrastructure.kafka.KafkaTopic
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class PostEventProducer(
    @Qualifier(KafkaProducerConfig.POST_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishCreatedEvent(post: PostResponse) {
        val event = PostEvent.created(post = post)
        eventHistoryManager.withSaveEventHistory(
            workspaceId = post.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = post.componentId,
            event = event,
        ) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.POST,
                key = KafkaRecordKeyGenerator.post(
                    workspaceId = post.workspaceId,
                    componentId = post.componentId,
                    postId = post.postId,
                ),
                data = event.toJson()
            )
        }
    }

    suspend fun publishModifiedEvent(post: PostResponse) {
        val event = PostEvent.modified(post = post)
        eventHistoryManager.withSaveEventHistory(
            workspaceId = post.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = post.componentId,
            event = event,
        ) {
            kafkaTemplate.send(
                kafkaTopic = KafkaTopic.POST,
                key = KafkaRecordKeyGenerator.post(
                    workspaceId = post.workspaceId,
                    componentId = post.componentId,
                    postId = post.postId,
                ),
                data = event.toJson()
            )
        }
    }

    suspend fun publishDeletedEvent(
        postSpaceKey: PostSpaceKey,
        postId: Long,
        accountId: String,
    ) {
        val event = PostEvent.deleted(
            workspaceId = postSpaceKey.workspaceId,
            componentId = postSpaceKey.componentId,
            spaceId = postSpaceKey.spaceId,
            postId = postId,
            accountId = accountId,
        )
        eventHistoryManager.withSaveEventHistory(
            workspaceId = postSpaceKey.workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = postSpaceKey.componentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(
                    kafkaTopic = KafkaTopic.POST,
                    key = KafkaRecordKeyGenerator.post(
                        workspaceId = postSpaceKey.workspaceId,
                        componentId = postSpaceKey.componentId,
                        postId = postId,
                    ),
                    data = event.toJson()
                )
            }
        }
    }

}
