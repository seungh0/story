package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.support.lock.DistributedLock
import com.story.platform.core.support.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
) {

    @DistributedLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun unsubscribe(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val subscriptionReverse = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        if (subscriptionReverse == null || subscriptionReverse.isDeleted()) {
            return false
        }

        val subscription = subscriberRepository.findById(
            SubscriberPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = subscriptionReverse.slotId,
                subscriberId = subscriberId,
            )
        )
        subscriptionReverse.delete()

        reactiveCassandraOperations.batchOps()
            .delete(subscription)
            .upsert(subscriptionReverse)
            .executeCoroutine()

        return true
    }

}
