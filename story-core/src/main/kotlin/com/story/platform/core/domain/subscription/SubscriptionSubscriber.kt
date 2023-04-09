package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.support.lock.DistributeLock
import com.story.platform.core.support.lock.DistributedLockType
import kotlinx.coroutines.reactor.awaitSingleOrNull
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

    @DistributeLock(
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
            .insert(subscriber)
            .insert(Subscription.of(subscriber = subscriber))
            .insert(SubscriberDistributed.of(subscriber = subscriber))
            .execute()
            .awaitSingleOrNull()
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
