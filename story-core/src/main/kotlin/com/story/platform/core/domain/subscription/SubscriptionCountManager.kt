package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.support.coroutine.CoroutineConfig.Companion.DEFAULT_TIMEOUT_MS
import com.story.platform.core.support.coroutine.IOBound
import kotlinx.coroutines.CoroutineDispatcher
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
    private val subscriptionsCountRepository: SubscriptionsCountRepository,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun increase(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val jobs = mutableListOf<Job>()
        withContext(dispatcher) {
            jobs += launch {
                withTimeout(DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionsCountRepository.increase(
                            SubscriptionsCountKey(
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
                withTimeout(DEFAULT_TIMEOUT_MS) {
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
        withContext(dispatcher) {
            jobs += launch {
                withTimeout(DEFAULT_TIMEOUT_MS) {
                    try {
                        subscriptionsCountRepository.decrease(
                            SubscriptionsCountKey(
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
                withTimeout(DEFAULT_TIMEOUT_MS) {
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
