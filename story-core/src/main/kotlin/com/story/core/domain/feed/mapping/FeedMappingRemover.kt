package com.story.core.domain.feed.mapping

import com.story.core.infrastructure.cassandra.executeCoroutine
import com.story.core.infrastructure.cassandra.upsert
import com.story.core.infrastructure.lock.DistributedLock
import com.story.core.infrastructure.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class FeedMappingRemover(
    private val feedMappingRepository: FeedMappingRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    @DistributedLock(
        lockType = DistributedLockType.FEED_MAPPING,
        key = "'workspaceId:' + {#request.workspaceId} + ':feedComponentId:' + {#request.feedComponentId} + ':sourceResourceId:' + {#request.sourceResourceId} + ':sourceComponentId:' + {#request.sourceComponentId}"
    )
    suspend fun remove(
        request: FeedMappingRemoveRequest,
    ) {
        val feedMapping = feedMappingRepository.findById(request.toConfigurationPrimaryKey())
            ?: throw FeedMappingAlreadyConnectedException("이미 워크스페이스(${request.workspaceId})의 리소스(${request.sourceResourceId})의 컴포넌트(${request.sourceComponentId})의 구독(${request.subscriptionComponentId})와 피드 연동 설정이 등록되어 있습니다")

        feedMapping.disconnect()

        reactiveCassandraOperations.batchOps()
            .upsert(feedMapping)
            .delete(FeedMappingReverse.of(feedMapping))
            .executeCoroutine()
    }

}
