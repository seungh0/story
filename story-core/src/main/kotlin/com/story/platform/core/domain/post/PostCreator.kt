package com.story.platform.core.domain.post

import com.story.platform.core.domain.post.section.PostSection
import com.story.platform.core.domain.post.section.PostSectionContentRequest
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostCreator(
    private val postSequenceRepository: PostSequenceRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createPost(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        title: String,
        sections: List<PostSectionContentRequest>,
    ): PostResponse {
        val postId = postSequenceRepository.generate(postSpaceKey = postSpaceKey)
        val post = Post.of(
            postSpaceKey = postSpaceKey,
            accountId = accountId,
            postId = postId,
            title = title,
        )

        val postSections = sections.map { section ->
            PostSection.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
                content = section.toSection(),
                sectionType = section.sectionType(),
                priority = section.priority,
            )
        }

        reactiveCassandraOperations.batchOps()
            .upsert(post)
            .upsert(PostReverse.of(post))
            .upsert(postSections)
            .executeCoroutine()

        return PostResponse.of(post = post, sections = postSections)
    }

}
