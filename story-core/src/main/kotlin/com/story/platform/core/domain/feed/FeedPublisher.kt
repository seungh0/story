package com.story.platform.core.domain.feed

import com.story.platform.core.domain.subscription.SubscriberDistributedEvent
import com.story.platform.core.domain.subscription.SubscriberRepository
import com.story.platform.core.domain.subscription.SubscribersCountRepository
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class FeedPublisher(
    private val subscriberRepository: SubscriberRepository,
    private val subscribersCountRepository: SubscribersCountRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    suspend fun createFeed(event: SubscriberDistributedEvent) {
        var pageable: Pageable = CassandraPageRequest.first(1000) // 1000 * 30 = 30_000 (최대 30번)

        do {
            val subscribers =
                subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                    workspaceId = event.workspaceId,
                    subscriptionType = event.subscriptionType,
                    targetId = event.targetId,
                    slotId = event.slot,
                    pageable = pageable
                )

            // TODO: 구독자들에게 피드를 발행한다

            // TODO: 알림을 킨 구독자들에게 푸시를 발송한다
            pageable = subscribers.nextPageable()
        } while (subscribers.hasNext())
    }

}
