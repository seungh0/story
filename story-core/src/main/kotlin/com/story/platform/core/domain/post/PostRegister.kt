package com.story.platform.core.domain.post

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostRegister(
    private val postIdGenerator: PostIdGenerator,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postEventPublisher: PostEventPublisher,
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

        postEventPublisher.publishCreatedEvent(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson,
        )
    }

}
