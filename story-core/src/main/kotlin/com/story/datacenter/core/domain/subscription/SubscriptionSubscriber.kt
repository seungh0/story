package com.story.datacenter.core.domain.subscription

import com.story.datacenter.core.common.enums.ServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
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
        extraJson: String? = null,
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

        withContext(Dispatchers.IO) {
            val jobs = mutableListOf<Job>()
            jobs.add(launch {
                val partitionId = subscriptionSlotAllocator.allocate(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
                val subscription = Subscription.of(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    partitionId = partitionId,
                    subscriberId = subscriberId,
                )
                val subscriptionReverse = SubscriptionReverse.of(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    subscriberId = subscriberId,
                    slotNo = partitionId,
                )

                reactiveCassandraOperations.batchOps()
                    .insert(subscription)
                    .insert(subscriptionReverse)
                    .execute()
                    .awaitSingleOrNull()
            })

            jobs.add(launch {
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

}
