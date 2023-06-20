package com.story.platform.core.domain.subscription

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
        key = "'workspaceId:' + {#workspaceId} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun subscribe(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ): Boolean {
        val subscriptionReverse = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
        if ((subscriptionReverse != null) && subscriptionReverse.isActivated()) {
            saveSubscription(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = subscriptionReverse.slotId,
                subscriberId = subscriberId,
                alarm = alarm,
            )
            return false
        }

        val slotId = subscriptionReverse?.slotId ?: SubscriptionSlotAssigner.assign(
            subscriberSequenceGenerator.generate(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId
            )
        )

        saveSubscription(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        return true
    }

    private suspend fun saveSubscription(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val subscriber = Subscriber.of(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
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
