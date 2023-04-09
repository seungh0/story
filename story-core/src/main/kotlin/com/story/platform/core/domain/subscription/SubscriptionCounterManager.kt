package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class SubscriptionCounterManager(
    private val subscribersCounterRepository: SubscribersCounterRepository,
    private val subscriptionsCounterRepository: SubscriptionsCounterRepository,
) {

    suspend fun increase(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val jobs = mutableListOf<Job>()
        withContext(Dispatchers.IO) {
            jobs += launch {
                subscriptionsCounterRepository.increase(
                    SubscriptionsCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                    )
                )
            }

            jobs += launch {
                subscribersCounterRepository.increase(
                    SubscribersCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                    )
                )
            }
        }
        jobs.joinAll()
    }

    suspend fun decrease(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val jobs = mutableListOf<Job>()
        withContext(Dispatchers.IO) {
            jobs += launch {
                subscriptionsCounterRepository.decrease(
                    SubscriptionsCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                    )
                )
            }

            jobs += launch {
                subscribersCounterRepository.decrease(
                    SubscribersCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                    )
                )
            }
        }
        jobs.joinAll()
    }

}
