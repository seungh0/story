package com.story.platform.core.domain.purge

import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriberPrimaryKey
import com.story.platform.core.domain.subscription.SubscriberRepository
import com.story.platform.core.domain.subscription.SubscriptionDistributionKey
import com.story.platform.core.domain.subscription.SubscriptionRepository
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SubscriptionPurger(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
) : Purger {

    override fun targetResourceId(): ResourceId = ResourceId.SUBSCRIPTIONS

    override fun distributeKeys(): Collection<DistributionKey> = SubscriptionDistributionKey.ALL_KEYS

    override suspend fun clear(workspaceId: String, componentId: String, distributionKey: DistributionKey): Long {
        var pageable: Pageable = CassandraPageRequest.first(500)
        var deletedCount = 0L
        do {
            val subscriptions = subscriptionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyOrderByKeyTargetIdAsc(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
                pageable = pageable,
            )

            val subscriberPrimaryKey = subscriptions.content.map { subscription ->
                SubscriberPrimaryKey.from(subscription = subscription)
            }

            subscriberRepository.deleteAllById(subscriberPrimaryKey)
            subscriptionRepository.deleteAllById(subscriptions.content.map { postReverse -> postReverse.key })

            deletedCount += subscriptions.size

            pageable = subscriptions.nextPageable()
        } while (subscriptions.hasNext())

        return deletedCount
    }

}
