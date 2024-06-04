package com.story.core.domain.subscription

import com.story.core.infrastructure.cassandra.CassandraCounterRepository
import org.springframework.data.cassandra.repository.Query

interface SubscriptionCountCassandraRepository : CassandraCounterRepository<SubscriptionCountEntity, SubscriptionCountPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTION_COUNT_V1} set count = count + :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and subscriber_id = :#{#key.subscriberId}
		"""
    )
    suspend fun increase(key: SubscriptionCountPrimaryKey, count: Long = 1L)

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTION_COUNT_V1} set count = count - :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and subscriber_id = :#{#key.subscriberId}
		"""
    )
    suspend fun decrease(key: SubscriptionCountPrimaryKey, count: Long = 1L)

}
