package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.post.PostSpaceType
import com.story.platform.core.domain.subscription.SubscriptionDistributedExecutor
import com.story.platform.core.domain.subscription.SubscriptionType
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class FeedRegister(
    private val subscriptionDistributedExecutor: SubscriptionDistributedExecutor,
    private val feedCoroutineRepository: FeedCoroutineRepository,
) {

    suspend fun registerPostFeed(
        serviceType: ServiceType,
        targetId: String,
        distributedKey: String,
        spaceType: PostSpaceType,
        spaceId: String,
        postId: Long,
    ) {
        subscriptionDistributedExecutor.executeToTargetSubscribers(
            serviceType = serviceType,
            distributedKey = distributedKey,
            targetId = targetId,
            runnableToSubscribers = { subscriptions ->
                val feeds = subscriptions.map { subscription ->
                    Feed.fromPost(
                        serviceType = serviceType,
                        accountId = subscription.key.subscriberId,
                        spaceType = spaceType,
                        spaceId = spaceId,
                        postId = postId,
                    )
                }
                feedCoroutineRepository.saveAll(feeds).toList()
            }
        )
    }

    suspend fun registerSubscriptionFeed(
        serviceType: ServiceType,
        distributedKey: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        subscriptionDistributedExecutor.executeToTargetSubscribers(
            serviceType = serviceType,
            distributedKey = distributedKey,
            targetId = targetId,
            runnableToSubscribers = { subscriptions ->
                val feeds = subscriptions.map { subscription ->
                    Feed.fromSubscription(
                        serviceType = serviceType,
                        accountId = subscription.key.subscriberId,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        subscriberId = subscriberId,
                    )
                }
                feedCoroutineRepository.saveAll(feeds).toList()
            }
        )
    }

}
