package com.story.pushcenter.core.domain.subscription

import com.story.pushcenter.core.common.enums.ServiceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionSlotAllocator: SubscriptionSlotAllocator,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
) {

    // TODO: 분산 락
    suspend fun subscribe(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        subscriberId: String,
        extraJson: String?,
    ) {
        val primaryKey = SubscriptionReversePrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        if (subscriptionReverseCoroutineRepository.existsById(primaryKey)) {
            return
        }

        val jobs = mutableListOf<Job>()
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            val slotNo = subscriptionSlotAllocator.allocate(subscriptionType = subscriptionType, targetId = targetId)
            val subscription = Subscription.of(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotNo = slotNo,
                subscriberId = subscriberId,
            )
            val subscriptionReverse = SubscriptionReverse.of(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = subscriberId,
                slotNo = slotNo,
            )

            reactiveCassandraOperations.batchOps()
                .insert(subscription)
                .insert(subscriptionReverse)
                .execute()
                .awaitSingleOrNull()
        })

        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            subscriptionCounterCoroutineRepository.increase(
                SubscriptionCounterPrimaryKey(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                )
            )
        })

        jobs.joinAll()
    }

}
