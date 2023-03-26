package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.json.toJson
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PostRemover(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    @Qualifier(KafkaProducerConfig.ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val postCoroutineRepository: PostCoroutineRepository,
    private val postReverseCoroutineRepository: PostReverseCoroutineRepository,
) {

    suspend fun remove(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
    ) {
        val postReverse =
            postReverseCoroutineRepository.findByKeyServiceTypeAndKeyAccountIdAndKeyPostIdAndKeySpaceTypeAndKeySpaceId(
                serviceType = postSpaceKey.serviceType,
                accountId = accountId,
                postId = postId,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
            ) ?: return

        val post = postCoroutineRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = postReverse.slotId,
            postId = postId,
        )

        reactiveCassandraOperations.batchOps()
            .delete(post)
            .delete(postReverse)
            .execute()
            .awaitSingleOrNull()

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
