package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriberCounterCoroutineRepository :
    CoroutineCrudRepository<SubscriberCounter, SubscriberCounterPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBER_COUNT} set count = count + :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun increase(
        key: SubscriberCounterPrimaryKey,
        count: Long = 1L,
    )

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIBER_COUNT} set count = count - :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and target_id = :#{#key.targetId}
		"""
    )
    suspend fun decrease(
        key: SubscriberCounterPrimaryKey,
        count: Long = 1L,
    )

}
