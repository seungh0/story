package com.story.core.domain.purge

import com.story.core.common.distribution.DistributionKey
import com.story.core.domain.resource.ResourceId
import com.story.core.domain.subscription.SubscriberRepository
import com.story.core.domain.subscription.Subscription
import com.story.core.domain.subscription.SubscriptionDistributionKey
import com.story.core.domain.subscription.SubscriptionRepository
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

            subscriptions.content.groupBy { subscription -> SubscriberPartitionKey.of(subscription) }.keys
                .forEach { key ->
                    subscriberRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotId(
                        workspaceId = key.workspaceId,
                        componentId = key.componentId,
                        targetId = key.targetId,
                        slotId = key.slotId,
                    )
                }

            subscriptionRepository.deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = distributionKey.key,
            )

            deletedCount += subscriptions.size

            pageable = subscriptions.nextPageable()
        } while (subscriptions.hasNext())

        return deletedCount
    }

    data class SubscriberPartitionKey(
        val workspaceId: String,
        val componentId: String,
        val targetId: String,
        val slotId: Long,
    ) {

        companion object {
            fun of(subscriber: Subscription) = SubscriberPartitionKey(
                workspaceId = subscriber.key.workspaceId,
                componentId = subscriber.key.componentId,
                targetId = subscriber.key.targetId,
                slotId = subscriber.slotId,
            )
        }

    }

}
