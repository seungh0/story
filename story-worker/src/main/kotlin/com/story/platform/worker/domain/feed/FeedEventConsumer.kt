package com.story.platform.worker.domain.feed

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.JsonUtils
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.feed.configuration.FeedReverseMappingConfigurationRepository
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriberDistributor
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class FeedEventConsumer(
    private val subscriberDistributor: SubscriberDistributor,
    private val feedReverseMappingConfigurationRepository: FeedReverseMappingConfigurationRepository,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    @KafkaListener(
        topics = ["\${story.kafka.post.topic}"],
        groupId = "\${story.kafka.post.group-id}",
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun handlePostEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = JsonUtils.toObject(record.value(), EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        val payload = JsonUtils.toObject(event.payload.toJson(), PostEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            var pageable: Pageable = CassandraPageRequest.first(100)
            do {
                val mappingConfigurations = feedReverseMappingConfigurationRepository.findAllByKeyWorkspaceIdAndKeySourceResourceIdAndKeySourceComponentIdAndKeyEventActionAndKeyTargetResourceId(
                    workspaceId = payload.workspaceId,
                    sourceResourceId = payload.resourceId,
                    sourceComponentId = payload.componentId,
                    eventAction = event.eventAction,
                    targetResourceId = ResourceId.SUBSCRIPTIONS,
                    pageable = pageable,
                )

                for (mappingConfiguration in mappingConfigurations) {
                    launch {
                        subscriberDistributor.distribute(
                            workspaceId = payload.workspaceId,
                            componentId = mappingConfiguration.key.targetComponentId,
                            targetId = payload.spaceId,
                        )
                    }
                }

                pageable = mappingConfigurations.nextPageable()
            } while (mappingConfigurations.hasNext())
        }
    }

    @KafkaListener(
        topics = ["\${story.kafka.subscription.topic}"],
        groupId = "\${story.kafka.subscription.group-id}",
        containerFactory = KafkaConsumerConfig.SUBSCRIPTION_CONTAINER_FACTORY,
    )
    fun handleSubscriptionEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = JsonUtils.toObject(record.value(), EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        val payload = JsonUtils.toObject(event.payload.toJson(), SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            subscriberDistributor.distribute(
                workspaceId = payload.workspaceId,
                componentId = payload.componentId,
                targetId = payload.subscriberId,
            )
        }
    }

}
