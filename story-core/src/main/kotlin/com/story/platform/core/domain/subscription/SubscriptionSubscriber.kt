package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
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
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val subscriptionIdGenerator: SubscriptionIdGenerator,
) {

    // TODO: 분산 락 적용
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
        val subscriptionReverse = subscriptionReverseCoroutineRepository.findById(primaryKey)
        if ((subscriptionReverse != null) && subscriptionReverse.isActivated()) {
            return
        }

        val subscription = Subscription.of(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = subscriptionReverse?.slotId ?: SubscriptionSlotAllocator.allocate(
                subscriptionIdGenerator.generate(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
            ),
            subscriberId = subscriberId,
            extraJson = extraJson,
        )

        withContext(Dispatchers.IO) {
            val jobs = mutableListOf<Job>()

            reactiveCassandraOperations.batchOps()
                .insert(subscription)
                .insert(SubscriptionReverse.of(subscription = subscription))
                .execute()
                .awaitSingleOrNull()

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
