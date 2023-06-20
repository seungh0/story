package com.story.platform.core.domain.feed.source

import com.story.platform.core.domain.feed.FeedMessage
import com.story.platform.core.domain.feed.FeedSourceType
import com.story.platform.core.domain.post.PostKey
import com.story.platform.core.domain.post.PostRetriever
import org.springframework.stereotype.Service

@Service
class PostFeedSourceImporter(
    private val postRetriever: PostRetriever,
) : FeedSourceImporter {

    override fun sourceType() = FeedSourceType.NEW_POST

    override suspend fun fetch(workspaceId: String, sourceIds: Collection<String>): List<FeedMessage> {
        val postKeys = sourceIds.map { postKey -> PostKey.of(postKey) }
        val posts = postRetriever.listPosts(
            workspaceId = workspaceId,
            keys = postKeys,
        )

        // TODO: FeedMessageTemplate 연동해서 title, content를 템플릿에 맞게 변환해준다..
        return posts.map { post -> FeedMessage.of(post) }
    }

}
