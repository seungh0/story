package com.story.platform.core.domain.post

import com.datastax.oss.driver.api.core.cql.BatchType
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostRegister(
    private val postIdGenerator: PostIdGenerator,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun register(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        content: String,
        extraJson: String? = null,
    ) {
        val postId = postIdGenerator.generate(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
        )
        val post = Post.of(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
            content = content,
            extraJson = extraJson
        )
        reactiveCassandraOperations.batchOps(BatchType.LOGGED)
            .insert(post)
            .insert(PostReverse.of(post))
            .execute()
            .awaitSingleOrNull()
    }

}
