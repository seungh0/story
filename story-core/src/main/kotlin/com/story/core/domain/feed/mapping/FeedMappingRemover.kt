package com.story.core.domain.feed.mapping

import com.story.core.infrastructure.cache.CacheEvict
import com.story.core.infrastructure.cache.CacheStrategy
import com.story.core.infrastructure.cache.CacheType
import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.lock.DistributedLock
import com.story.core.infrastructure.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedMappingRemover(
    private val feedMappingRepository: FeedMappingRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    @CacheEvict(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#request.workspaceId} + ':sourceResourceId:' + {#request.sourceResourceId} + ':sourceComponentId:' + {#request.sourceComponentId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    @DistributedLock(
        lockType = DistributedLockType.FEED_MAPPING,
        key = "'workspaceId:' + {#request.workspaceId} + ':feedComponentId:' + {#request.feedComponentId} + ':sourceResourceId:' + {#request.sourceResourceId} + ':sourceComponentId:' + {#request.sourceComponentId}"
    )
    suspend fun remove(
        request: FeedMappingRemoveRequest,
    ) {
        val feedMapping = feedMappingRepository.findById(request.toConfigurationPrimaryKey())
            ?: throw FeedMappingNotExistsException("워크스페이스(${request.workspaceId})의 리소스(${request.sourceResourceId})의 컴포넌트(${request.sourceComponentId})의 구독(${request.subscriptionComponentId})와 피드 연동 설정이 존재하지 않습니다")

        reactiveCassandraOperations.batchOps()
            .delete(feedMapping)
            .delete(FeedMappingReverse.of(feedMapping))
            .executeCoroutine()
    }

}
