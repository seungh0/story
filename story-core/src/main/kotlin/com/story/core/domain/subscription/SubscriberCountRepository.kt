package com.story.core.domain.subscription

import com.story.core.infrastructure.cassandra.CassandraCounterRepository
import org.springframework.data.cassandra.repository.Query

interface SubscriberCountRepository : CassandraCounterRepository<SubscriberCountEntity, SubscriberCountPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBER_COUNT_V1} set count = count + :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun increase(key: SubscriberCountPrimaryKey, count: Long = 1L)

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBER_COUNT_V1} set count = count - :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun decrease(key: SubscriberCountPrimaryKey, count: Long = 1L)

}
