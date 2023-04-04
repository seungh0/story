package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SubscriberDistributedExecutor(
    private val subscriberDistributedReactiveRepository: SubscriberDistributedReactiveRepository,
) {

    suspend fun executeToTargetSubscribers(
        serviceType: ServiceType,
        distributedKey: String,
        targetId: String,
        fetchSize: Int = 100,
        runnableToSubscribers: suspend (subscriptions: Collection<SubscriberDistributed>) -> Unit,
    ) {
        var pageable: Pageable = CassandraPageRequest.first(fetchSize)

        do {
            val subscriptions =
                subscriberDistributedReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyDistributedKeyAndKeyTargetId(
                    serviceType = serviceType,
                    subscriptionType = SubscriptionType.FOLLOW,
                    distributedKey = distributedKey,
                    targetId = targetId,
                    pageable = pageable,
                )

            runnableToSubscribers.invoke(subscriptions.content)

            if (subscriptions.hasNext()) {
                pageable = subscriptions.nextPageable()
            }
        } while (subscriptions.hasNext())
    }

}
