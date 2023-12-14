package com.story.platform.core.domain.subscription

import com.story.platform.core.common.coroutine.toMutableList
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.Slice
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.common.model.dto.CursorResponse
import com.story.platform.core.common.utils.CursorUtils
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class SubscriptionRetriever(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
    private val subscriberSequenceRepository: SubscriberSequenceRepository,
) {

    suspend fun existsSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val primaryKey = SubscriptionPrimaryKey.of(
            workspaceId = workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        val subscription = subscriptionRepository.findById(primaryKey)
        return subscription != null && subscription.isActivated()
    }

    suspend fun listSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): Slice<SubscriptionResponse, String> {
        val response = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> listPreviousSubscribers(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        }
        return Slice.of(
            data = response.data.map { subscriber -> SubscriptionResponse.of(subscriber) },
            cursor = response.cursor,
        )
    }

    private suspend fun listPreviousSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): Slice<Subscriber, String> {
        val firstSlotId = SubscriptionSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAssigner.assign(
            subscriberSequenceRepository.getLastSequence(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetId(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = SubscriptionDistributionKey.makeKey(cursor),
                subscriberId = cursor,
                targetId = targetId,
            )?.slotId
        } ?: lastSlotId

        val subscribers = if (cursorRequest.cursor == null) {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        } else {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }.toMutableList()

        var previousCursor = CursorUtils.getCursor(
            listWithNextCursor = subscribers,
            pageSize = cursorRequest.pageSize,
            keyGenerator = { subscription -> subscription?.key?.subscriberId }
        )

        if (!previousCursor.hasNext && currentSlotId > firstSlotId) {
            previousCursor = CursorResponse.of(cursor = subscribers.lastOrNull()?.key?.subscriberId)
        }

        if (subscribers.size >= cursorRequest.pageSize) {
            return Slice.of(
                data = subscribers.subList(0, cursorRequest.pageSize.coerceAtMost(subscribers.size)),
                cursor = previousCursor,
            )
        }

        while (subscribers.size < cursorRequest.pageSize && --currentSlotId >= firstSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscribersInSlotWithNextCursor =
                subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    slotId = currentSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                ).toList()

            subscribers += subscribersInSlotWithNextCursor.subList(
                0,
                needMoreSize.coerceAtMost(subscribersInSlotWithNextCursor.size)
            )

            previousCursor = CursorUtils.getCursor(
                listWithNextCursor = subscribersInSlotWithNextCursor,
                pageSize = needMoreSize,
                keyGenerator = { subscriber -> subscriber?.key?.subscriberId }
            )
        }

        return Slice.of(
            data = subscribers,
            cursor = previousCursor
        )
    }

    private suspend fun listNextSubscribers(
        workspaceId: String,
        componentId: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): Slice<Subscriber, String> {
        val firstSlotId = SubscriptionSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAssigner.assign(
            subscriberSequenceRepository.getLastSequence(
                workspaceId,
                componentId,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionRepository.findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetId(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = SubscriptionDistributionKey.makeKey(cursor),
                subscriberId = cursor,
                targetId = targetId,
            )?.slotId
        } ?: firstSlotId

        val subscribers = if (cursorRequest.cursor == null) {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        } else {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }.toMutableList()

        var nextCursor = CursorUtils.getCursor(
            listWithNextCursor = subscribers,
            pageSize = cursorRequest.pageSize,
            keyGenerator = { subscription -> subscription?.key?.subscriberId }
        )

        if (!nextCursor.hasNext && currentSlotId < lastSlotId) {
            nextCursor = CursorResponse.of(cursor = subscribers.lastOrNull()?.key?.subscriberId)
        }

        if (subscribers.size >= cursorRequest.pageSize) {
            return Slice.of(
                data = subscribers.subList(0, cursorRequest.pageSize.coerceAtMost(subscribers.size)),
                cursor = nextCursor,
            )
        }

        while (subscribers.size < cursorRequest.pageSize && ++currentSlotId <= lastSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscribersInSlotWithNextCursor =
                subscriberRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdAsc(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    targetId = targetId,
                    slotId = currentSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                ).toList()

            subscribers += subscribersInSlotWithNextCursor
                .subList(0, needMoreSize.coerceAtMost(subscribersInSlotWithNextCursor.size))

            nextCursor = CursorUtils.getCursor(
                listWithNextCursor = subscribersInSlotWithNextCursor,
                pageSize = needMoreSize,
                keyGenerator = { subscriber -> subscriber?.key?.subscriberId }
            )
        }

        return Slice.of(
            data = subscribers,
            cursor = nextCursor
        )
    }

    suspend fun listSubscriptionTargets(
        workspaceId: String,
        componentId: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): Slice<SubscriptionResponse, String> {
        val subscriptions = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextSubscriptionTargets(
                cursorRequest = cursorRequest,
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
            )

            CursorDirection.PREVIOUS -> listPreviousSubscriptionTargets(
                cursorRequest = cursorRequest,
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId
            )
        }

        val data = when (cursorRequest.direction) {
            CursorDirection.NEXT ->
                subscriptions.subList(0, (cursorRequest.pageSize).coerceAtMost(subscriptions.size))

            CursorDirection.PREVIOUS ->
                subscriptions.subList(0, (cursorRequest.pageSize).coerceAtMost(subscriptions.size)).reversed()
        }

        return Slice.of(
            data = data.map { subscription -> SubscriptionResponse.of(subscription) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = subscriptions,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { subscription -> subscription?.key?.targetId }
            )
        )
    }

    private suspend fun listNextSubscriptionTargets(
        cursorRequest: CursorRequest,
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): List<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdAsc(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            ).toList()
        }
        return subscriptionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
        ).toList()
    }

    private suspend fun listPreviousSubscriptionTargets(
        cursorRequest: CursorRequest,
        workspaceId: String,
        componentId: String,
        subscriberId: String,
    ): List<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdDesc(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            ).toList()
        }

        return subscriptionRepository.findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
            workspaceId = workspaceId,
            componentId = componentId,
            distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
        ).toList()
    }

}
