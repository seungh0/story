package com.story.core.domain.subscription

import com.story.core.support.cassandra.executeCoroutine
import com.story.core.support.cassandra.upsert
import com.story.core.support.lock.DistributedLock
import com.story.core.support.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberSequenceRepository: SubscriberSequenceRepository,
) {

    @DistributedLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun upsertSubscription(
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
                distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
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
            subscriberSequenceRepository.generate(
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
        val subscriber = SubscriberEntity.of(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(subscriber)
            .upsert(SubscriptionEntity.of(subscriber = subscriber))
            .executeCoroutine()
    }

}
