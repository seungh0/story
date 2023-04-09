package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriberDistributedRepository :
    CoroutineCrudRepository<SubscriberDistributed, SubscriberDistributedPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyDistributedKeyAndKeyTargetId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        distributedKey: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriberDistributed>

}
