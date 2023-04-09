package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
    private val subscriberDistributedRepository: SubscriberDistributedRepository,
    private val subscriptionCounterManager: SubscriptionCounterManager,
    private val subscriptionEventPublisher: SubscriptionEventPublisher,
) {

    suspend fun unsubscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val subscriptionReverse = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        if (subscriptionReverse == null || subscriptionReverse.isDeleted()) {
            return
        }

        val subscription = subscriberRepository.findById(
            SubscriberPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = subscriptionReverse.slotId,
                subscriberId = subscriberId,
            )
        )
        subscriptionReverse.delete()

        val subscriptionDistributed = subscriberDistributedRepository.findById(
            SubscriberDistributedPrimaryKey.of(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        )

        reactiveCassandraOperations.batchOps()
            .delete(subscription)
            .insert(subscriptionReverse)
            .delete(subscriptionDistributed)
            .execute()
            .awaitSingleOrNull()

        unsubscriptionPostProcessor(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )
    }

    private suspend fun unsubscriptionPostProcessor(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        subscriptionCounterManager.decrease(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        subscriptionEventPublisher.publishUnsubscriptionEvent(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

}
