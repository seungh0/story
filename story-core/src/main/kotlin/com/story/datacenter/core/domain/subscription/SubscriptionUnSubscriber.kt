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
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
) {

    suspend fun unsubscribe(
        serviceType: ServiceType,
        subscriptionType: String,
        targetId: String,
        subscriberId: String,
    ) {

        val subscriptionReverse = subscriptionReverseCoroutineRepository.findById(
            SubscriptionReversePrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        ) ?: return

        val jobs = mutableListOf<Job>()
        withContext(Dispatchers.IO) {
            jobs.add(launch {
                val subscription = subscriptionCoroutineRepository.findById(
                    SubscriptionPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotNo = subscriptionReverse.slotNo,
                        subscriberId = subscriberId,
                    )
                )
                reactiveCassandraOperations.batchOps()
                    .delete(subscriptionReverse)
                    .delete(subscription)
                    .execute()
                    .awaitSingleOrNull()
            })

            jobs.add(launch {
                subscriptionCounterCoroutineRepository.decrease(
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
