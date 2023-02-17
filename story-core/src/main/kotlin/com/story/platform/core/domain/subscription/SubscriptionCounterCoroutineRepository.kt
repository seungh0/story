package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionCounterCoroutineRepository :
    CoroutineCrudRepository<SubscriptionCounter, SubscriptionCounterPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTION_COUNTER} set count = count + :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun increase(
        key: SubscriptionCounterPrimaryKey,
        count: Long = 1L,
    )

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTION_COUNTER} set count = count - :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun decrease(
        key: SubscriptionCounterPrimaryKey,
        count: Long = 1L,
    )

}
