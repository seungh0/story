package com.story.platform.core.domain.feed

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.post.PostSpaceType
import com.story.platform.core.domain.subscription.SubscriberDistributedExecutor
import com.story.platform.core.domain.subscription.SubscriptionType
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class FeedRegister(
    private val subscriberDistributedExecutor: SubscriberDistributedExecutor,
    private val feedRepository: FeedRepository,
) {

    suspend fun addPostFeed(
        serviceType: ServiceType,
        targetId: String,
        distributedKey: String,
        spaceType: PostSpaceType,
        spaceId: String,
        postId: String,
    ) {
        subscriberDistributedExecutor.executeToTargetSubscribers(
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
                feedRepository.saveAll(feeds).toList()
            }
        )
    }

    suspend fun addSubscriptionFeed(
        serviceType: ServiceType,
        distributedKey: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        subscriberDistributedExecutor.executeToTargetSubscribers(
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
                feedRepository.saveAll(feeds).toList()
            }
        )
    }

}
