package com.story.core.domain.subscription

import com.story.core.support.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionRepository :
    CassandraBasicRepository<SubscriptionEntity, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
    ): SubscriptionEntity?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        pageable: Pageable,
    ): Slice<SubscriptionEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<SubscriptionEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<SubscriptionEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<SubscriptionEntity>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<SubscriptionEntity>

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
    )

}
