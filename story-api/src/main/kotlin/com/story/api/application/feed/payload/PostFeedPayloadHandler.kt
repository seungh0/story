package com.story.api.application.feed.payload

import com.story.api.application.post.PostApiResponse
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.feed.FeedResponse
import com.story.core.domain.post.PostEventKey
import com.story.core.domain.post.PostNotExistsException
import com.story.core.domain.post.PostRetriever
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostFeedPayloadHandler(
    private val postRetriever: PostRetriever,
) : FeedPayloadHandler {

    override fun resourceId(): ResourceId = ResourceId.POSTS

    override suspend fun handle(
        workspaceId: String,
        feeds: Collection<FeedResponse>,
        requestAccountId: String?,
    ): Map<Long, FeedPayload> {
        return feeds.mapNotNull { feed ->
            try {
                val eventKey = PostEventKey.parse(eventKey = feed.eventKey)
                feed.feedId to PostApiResponse.of(
                    postRetriever.getPost(
                        postSpaceKey = PostSpaceKey(
                            workspaceId = workspaceId,
                            componentId = feed.sourceComponentId,
                            spaceId = eventKey.spaceId,
                        ),
                        postId = eventKey.postId,
                    ),
                    requestAccountId = requestAccountId,
                )
            } catch (exception: PostNotExistsException) {
                return@mapNotNull null
            }
        }.associate { it.first to it.second }
    }

}
