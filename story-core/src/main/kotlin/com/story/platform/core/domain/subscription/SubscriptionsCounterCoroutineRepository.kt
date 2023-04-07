package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionsCounterCoroutineRepository :
    CoroutineCrudRepository<SubscriptionsCounter, SubscriptionsCounterPrimaryKey> {

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTIONS_COUNTER} set count = count + :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and subscriber_id = :#{#key.subscriberId}
		"""
    )
    suspend fun increase(
        key: SubscriptionsCounterPrimaryKey,
        count: Long = 1L,
    )

    @Query(
        """
			update ${SubscriptionTableNames.SUBSCRIPTIONS_COUNTER} set count = count - :count
			where service_type = :#{#key.serviceType.name()}
			and subscription_type = :#{#key.subscriptionType.name()}
			and subscriber_id = :#{#key.subscriberId}
		"""
    )
    suspend fun decrease(
        key: SubscriptionsCounterPrimaryKey,
        count: Long = 1L,
    )

}
