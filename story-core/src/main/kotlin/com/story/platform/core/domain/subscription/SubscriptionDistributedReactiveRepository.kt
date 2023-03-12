package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionDistributedReactiveRepository :
    ReactiveCassandraRepository<SubscriptionDistributed, SubscriptionDistributedPrimaryKey> {

    suspend fun findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyDistributedKeyAndKeyTargetId(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        distributedKey: String,
        targetId: String,
        pageable: Pageable,
    ): Slice<SubscriptionDistributed>

}
