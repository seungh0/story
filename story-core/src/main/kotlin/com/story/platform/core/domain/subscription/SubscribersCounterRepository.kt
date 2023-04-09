package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscribersCounterRepository :
    CoroutineCrudRepository<SubscribersCounter, SubscribersCounterPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBERS_COUNTER} set count = count + :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun increase(
        key: SubscribersCounterPrimaryKey,
        count: Long = 1L,
    )

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBERS_COUNTER} set count = count - :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun decrease(
        key: SubscribersCounterPrimaryKey,
        count: Long = 1L,
    )

}
