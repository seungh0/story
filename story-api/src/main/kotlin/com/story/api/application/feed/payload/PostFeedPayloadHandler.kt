package com.story.api.application.feed.payload

import com.story.api.application.post.PostResponse
import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedItem
import com.story.core.domain.feed.FeedPayload
import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostNotExistsException
import com.story.core.domain.post.PostReaderWithCache
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.resource.ResourceId
import org.springframework.stereotype.Service

@Service
class PostFeedPayloadHandler(
    private val postReaderWithCache: PostReaderWithCache,
) : FeedPayloadHandler {

    override fun resourceId(): ResourceId = ResourceId.POSTS

    override suspend fun handle(
        workspaceId: String,
        feeds: Collection<Feed>,
        requestUserId: String?,
    ): Map<FeedItem, FeedPayload> {
        return feeds.mapNotNull { feed ->
            val postId = PostId.parsed(feed.item.itemId)
            try {
                feed.item to PostResponse.of(
                    postReaderWithCache.getPost(
                        postSpaceKey = PostSpaceKey(
                            workspaceId = workspaceId,
                            componentId = feed.item.componentId,
                            spaceId = postId.spaceId,
                        ),
                        postId = postId,
                    ),
                    requestUserId = requestUserId,
                )
            } catch (exception: PostNotExistsException) {
                return@mapNotNull null
            }
        }.associate { it.first to it.second }
    }

}
