package com.story.platform.core.domain.subscription

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class SubscriptionCountManager(
    private val subscriberCountRepository: SubscriberCountRepository,
    private val subscriptionCountRepository: SubscriptionCountRepository,
) {

    suspend fun increase(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ) = coroutineScope {
        val jobs = mutableListOf<Job>()
        jobs += launch {
            subscriptionCountRepository.increase(
                SubscriptionCountPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                )
            )
        }

        jobs += launch {
            subscriberCountRepository.increase(
                key = SubscriberCountPrimaryKey(
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
            subscriptionCountRepository.decrease(
                SubscriptionCountPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                )
            )
        }

        jobs += launch {
            subscriberCountRepository.decrease(
                key = SubscriberCountPrimaryKey(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                )
            )
        }
        jobs.joinAll()
    }

}
