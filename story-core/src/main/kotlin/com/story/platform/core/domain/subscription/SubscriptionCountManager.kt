package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.support.coroutine.CoroutineConfigConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Service

@Service
class SubscriptionCountManager(
    private val subscribersCountRepository: SubscribersCountRepository,
    private val subscriptionCountRepository: SubscriptionCountRepository,
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
                withTimeout(CoroutineConfigConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionCountRepository.increase(
                            SubscriptionCountKey(
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
                withTimeout(CoroutineConfigConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscribersCountRepository.increase(
                            SubscribersCountKey(
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
                withTimeout(CoroutineConfigConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionCountRepository.decrease(
                            SubscriptionCountKey(
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
                withTimeout(CoroutineConfigConstants.DEFAULT_TIMEOUT_MS) {
                    try {
                        subscribersCountRepository.decrease(
                            SubscribersCountKey(
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
