package com.story.platform.core.domain.subscription

import com.story.platform.core.common.distribution.XLargeDistributionKey
import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.support.lock.DistributedLock
import com.story.platform.core.support.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberSequenceGenerator: SubscriberSequenceGenerator,
) {

    @DistributedLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun upsert(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ): Boolean {
        val subscription = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = XLargeDistributionKey.makeKey(subscriberId).key,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
        if ((subscription != null) && subscription.isActivated()) {
            saveSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = subscription.slotId,
                subscriberId = subscriberId,
                alarm = alarm,
            )
            return false
        }

        val slotId = subscription?.slotId ?: SubscriptionSlotAssigner.assign(
            subscriberSequenceGenerator.generate(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId
            )
        )

        saveSubscription(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        return true
    }

    private suspend fun saveSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val subscriber = Subscriber.of(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(subscriber)
            .upsert(Subscription.of(subscriber = subscriber))
            .executeCoroutine()
    }

}
