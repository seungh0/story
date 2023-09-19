package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionCountRepository : CoroutineCrudRepository<SubscriptionCount, SubscriptionCountPrimaryKey> {

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
