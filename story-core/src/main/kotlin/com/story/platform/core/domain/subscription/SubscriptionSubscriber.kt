package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
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
    private val subscriberIdGenerator: SubscriberIdGenerator,
    private val subscriptionCounterManager: SubscriptionCounterManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    @DistributedLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun subscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val subscriptionReverse = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )
        if ((subscriptionReverse != null) && subscriptionReverse.isActivated()) {
            saveSubscription(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = subscriptionReverse.slotId,
                subscriberId = subscriberId,
                alarm = alarm,
            )
            return
        }

        saveSubscription(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = subscriptionReverse?.slotId ?: SubscriberSlotAssigner.assign(
                subscriberIdGenerator.generate(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
            ),
            subscriberId = subscriberId,
            alarm = alarm,
        )

        newSubscriptionPostProcessor(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
    }

    private suspend fun saveSubscription(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val subscriber = Subscriber.of(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        reactiveCassandraOperations.batchOps()
            .upsert(subscriber)
            .upsert(Subscription.of(subscriber = subscriber))
            .upsert(SubscriberDistributed.of(subscriber = subscriber))
            .executeCoroutine()
    }

    private suspend fun newSubscriptionPostProcessor(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        subscriptionCounterManager.increase(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        subscriptionEventPublisher.publishSubscriptionEvent(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

}
