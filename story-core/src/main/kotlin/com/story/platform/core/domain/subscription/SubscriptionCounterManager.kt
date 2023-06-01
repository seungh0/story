package com.story.platform.core.domain.subscription

import com.story.platform.core.common.constants.CoroutineConstants
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.InternalServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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
                withTimeout(CoroutineConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionsCounterRepository.increase(
                            SubscriptionsCounterPrimaryKey(
                                serviceType = serviceType,
                                subscriptionType = subscriptionType,
                                subscriberId = subscriberId,
                            )
                        )
                    } catch (exception: TimeoutCancellationException) {
                        throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                    }
                }
            }

            jobs += launch {
                withTimeout(CoroutineConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscribersCounterRepository.increase(
                            SubscribersCounterPrimaryKey(
                                serviceType = serviceType,
                                subscriptionType = subscriptionType,
                                targetId = targetId,
                            )
                        )
                    } catch (exception: TimeoutCancellationException) {
                        throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                    }
                }
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
                withTimeout(CoroutineConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionsCounterRepository.decrease(
                            SubscriptionsCounterPrimaryKey(
                                serviceType = serviceType,
                                subscriptionType = subscriptionType,
                                subscriberId = subscriberId,
                            )
                        )
                    } catch (exception: TimeoutCancellationException) {
                        throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                    }
                }
            }

            jobs += launch {
                withTimeout(CoroutineConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscribersCounterRepository.decrease(
                            SubscribersCounterPrimaryKey(
                                serviceType = serviceType,
                                subscriptionType = subscriptionType,
                                targetId = targetId,
                            )
                        )
                    } catch (exception: TimeoutCancellationException) {
                        throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                    }
                }
            }
        }
        jobs.joinAll()
    }

}
