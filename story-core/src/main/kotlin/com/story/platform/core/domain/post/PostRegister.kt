package com.story.platform.core.domain.post

import com.story.platform.core.support.json.JsonUtils
import com.story.platform.core.support.kafka.KafkaProducerConfig
import com.story.platform.core.support.kafka.KafkaTopicFinder
import com.story.platform.core.support.kafka.TopicType
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class PostRegister(
    private val postIdGenerator: PostIdGenerator,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    @Qualifier(KafkaProducerConfig.ACK_ALL_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun register(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extraJson: String? = null,
    ) {
        val postId = postIdGenerator.generate(postSpaceKey = postSpaceKey)
        val post = Post.of(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
        reactiveCassandraOperations.batchOps()
            .insert(post)
            .insert(PostReverse.of(post))
            .execute()
            .awaitSingleOrNull()

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
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.POST), JsonUtils.toJson(event))
    }

}
