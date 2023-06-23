package com.story.platform.core.domain.subscription

import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.support.coroutine.CoroutineConfig.Companion.DEFAULT_TIMEOUT_MS
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Service

@Service
class SubscriptionCountManager(
    private val subscribersCountRepository: SubscribersCountRepository,
    private val subscriptionsCountRepository: SubscriptionsCountRepository,
) {

    suspend fun increase(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ) = coroutineScope {
        val jobs = mutableListOf<Job>()
        jobs += launch {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                try {
                    subscriptionsCountRepository.increase(
                        SubscriptionsCountKey(
                            workspaceId = workspaceId,
                            componentId = componentId,
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
                            workspaceId = workspaceId,
                            componentId = componentId,
                            targetId = targetId,
                        )
                    )
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }
        jobs.joinAll()
    }

    suspend fun decrease(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ) = coroutineScope {
        val jobs = mutableListOf<Job>()
        jobs += launch {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                try {
                    subscriptionsCountRepository.decrease(
                        SubscriptionsCountKey(
                            workspaceId = workspaceId,
                            componentId = componentId,
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
                            workspaceId = workspaceId,
                            componentId = componentId,
                            targetId = targetId,
                        )
                    )
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(exception.message ?: "Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }
        jobs.joinAll()
    }

}
