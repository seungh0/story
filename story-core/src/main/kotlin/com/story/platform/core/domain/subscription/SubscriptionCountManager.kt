package com.story.platform.core.domain.subscription

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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
            subscriptionsCountRepository.increase(
                SubscriptionsCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                )
            )
        }

        jobs += launch {
            subscribersCountRepository.increase(
                SubscribersCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                )
            )
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
            subscriptionsCountRepository.decrease(
                SubscriptionsCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                )
            )
        }

        jobs += launch {
            subscribersCountRepository.decrease(
                SubscribersCountKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                )
            )
        }
        jobs.joinAll()
    }

}
