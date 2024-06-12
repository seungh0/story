package com.story.core.domain.feed.mapping

import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import com.story.core.support.lock.DistributedLock
import com.story.core.support.lock.DistributedLockType
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedMappingCreator(
    private val feedMappingReadRepository: FeedMappingReadRepository,
    private val feedMappingWriteRepository: FeedMappingWriteRepository,
) {

    @CacheEvict(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#command.workspaceId} + ':sourceResourceId:' + {#command.sourceResourceId} + ':sourceComponentId:' + {#command.sourceComponentId}",
        targetCacheStrategies = [CacheStrategy.GLOBAL]
    )
    @DistributedLock(
        lockType = DistributedLockType.FEED_MAPPING,
        key = "'workspaceId:' + {#command.workspaceId} + ':feedComponentId:' + {#command.feedComponentId} + ':sourceResourceId:' + {#command.sourceResourceId} + ':sourceComponentId:' + {#command.sourceComponentId}"
    )
    suspend fun create(
        command: FeedMappingCreateCommand,
    ) {
        validateNotExistsFeedMapping(command)
        validateNotExceededFeedMappingSize(command)

        feedMappingWriteRepository.create(
            workspaceId = command.workspaceId,
            feedComponentId = command.feedComponentId,
            sourceResourceId = command.sourceResourceId,
            sourceComponentId = command.sourceComponentId,
            subscriptionComponentId = command.subscriptionComponentId,
            description = command.description,
            retention = command.retention,
        )
    }

    private suspend fun validateNotExceededFeedMappingSize(command: FeedMappingCreateCommand) {
        val feeds = feedMappingReadRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = command.workspaceId,
            feedComponentId = command.feedComponentId,
            sourceResourceId = command.sourceResourceId,
            sourceComponentId = command.sourceComponentId,
            pageable = CassandraPageRequest.first(3)
        )

        if (feeds.numberOfElements >= 3) {
            throw FeedMappingCapacityExceededException("워크스페이스(${command.workspaceId})에서 FeedComponent(${command.feedComponentId})의 Source(${command.sourceResourceId}-${command.sourceComponentId})에서 발행할 수 있는 최대 연결은 3개로 제한됩니다")
        }
    }

    private suspend fun validateNotExistsFeedMapping(command: FeedMappingCreateCommand) {
        if (feedMappingReadRepository.existsById(
                workspaceId = command.workspaceId,
                feedComponentId = command.feedComponentId,
                sourceResourceId = command.sourceResourceId,
                sourceComponentId = command.sourceComponentId,
                subscriptionComponentId = command.subscriptionComponentId,
            )
        ) {
            throw FeedMappingAlreadyConnectedException("이미 워크스페이스(${command.workspaceId})의 리소스(${command.sourceResourceId})의 컴포넌트(${command.sourceComponentId})의 구독(${command.subscriptionComponentId})와 피드 연동 설정이 등록되어 있습니다")
        }
    }

}
