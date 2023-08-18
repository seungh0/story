package com.story.platform.core.domain.post

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostCreator(
    private val postSequenceGenerator: PostSequenceGenerator,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun create(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extra: Map<String, String>,
    ): PostResponse {
        val postId = postSequenceGenerator.generate(postSpaceKey = postSpaceKey)
        val post = Post.of(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extra = extra,
        )
        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .executeCoroutine()

        return PostResponse.of(post = post)
    }

}
