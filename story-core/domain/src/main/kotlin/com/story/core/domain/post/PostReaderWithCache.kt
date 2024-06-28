package com.story.core.domain.post

import com.story.core.support.cache.CacheType
import com.story.core.support.cache.Cacheable
import org.springframework.stereotype.Service

@Service
class PostReaderWithCache(
    private val postReader: PostReader,
) {

    @Cacheable(
        cacheType = CacheType.POST,
        key = "'workspaceId:' + {#postSpaceKey.workspaceId} + ':componentId:' + {#postSpaceKey.componentId} + ':spaceId:' + {#postSpaceKey.spaceId} + ':parentId:' + {#postId.parentId} + ':postNo:' + {#postId.postNo}",
    )
    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: PostId,
    ): PostWithSections {
        return postReader.getPost(
            postSpaceKey = postSpaceKey,
            postId = postId,
        )
    }

}
