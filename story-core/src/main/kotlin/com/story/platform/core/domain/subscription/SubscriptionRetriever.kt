package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class SubscriptionRetriever(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
    private val subscriberSequenceGenerator: SubscriberSequenceGenerator,
) {

    suspend fun isSubscriber(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val primaryKey = SubscriptionPrimaryKey(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        val subscription = subscriptionRepository.findById(primaryKey)
        return subscription != null && subscription.isActivated()
    }

    suspend fun listTargetSubscribers(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriptionResponse, String> {
        val response = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextSubscribers(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> listPreviousSubscribers(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        }
        return CursorResult.of(
            data = response.data.map { subscriber -> SubscriptionResponse.of(subscriber) },
            cursor = response.cursor,
        )
    }

    private suspend fun listPreviousSubscribers(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscriber, String> {
        val firstSlotId = SubscriptionSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAssigner.assign(
            subscriberSequenceGenerator.lastSequence(
                workspaceId,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionRepository.findByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: lastSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdLessThan(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        var previousCursor = SubscriberCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
        if (previousCursor == null && currentSlotId > firstSlotId) {
            subscriptionSlice.content.lastOrNull()?.key?.subscriberId ?: cursorRequest.cursor
        }

        if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
            return CursorResult.of(
                data = subscriptionSlice.content,
                cursor = Cursor.of(cursor = previousCursor),
            )
        }

        val subscribers = subscriptionSlice.content as MutableList<Subscriber>

        while (subscribers.size < cursorRequest.pageSize && --currentSlotId >= firstSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscriptionsInCurrentSlot =
                subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
                    workspaceId = workspaceId,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    slotId = lastSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                )

            val sizeOfCurrentCursor = needMoreSize.coerceAtMost(subscriptionSlice.size)
            val subscriptionInCurrentCursor = subscriptionsInCurrentSlot.content.subList(0, sizeOfCurrentCursor)
            subscribers += subscriptionInCurrentCursor

            previousCursor =
                if (subscriptionsInCurrentSlot.size > needMoreSize || currentSlotId > firstSlotId) {
                    subscriptionInCurrentCursor.lastOrNull()?.key?.subscriberId
                } else {
                    null
                }
        }

        return CursorResult.of(
            data = subscribers,
            cursor = Cursor.of(cursor = previousCursor)
        )
    }

    private suspend fun listNextSubscribers(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscriber, String> {
        val firstSlotId = SubscriptionSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAssigner.assign(
            subscriberSequenceGenerator.lastSequence(
                workspaceId,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionRepository.findByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: firstSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        var nextCursor: String? = SubscriberCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
        if (nextCursor == null && currentSlotId < lastSlotId) {
            subscriptionSlice.content.lastOrNull()?.key?.subscriberId ?: cursorRequest.cursor
        }

        if (subscriptionSlice.size >= cursorRequest.pageSize) {
            return CursorResult.of(
                data = subscriptionSlice.content,
                cursor = Cursor.of(cursor = nextCursor),
            )
        }

        val subscribers = subscriptionSlice.content as MutableList<Subscriber>

        while (cursorRequest.pageSize > subscribers.size && ++currentSlotId <= lastSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscriptionsInCurrentSlot =
                subscriberRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                    workspaceId = workspaceId,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    slotId = currentSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                )

            val sizeOfCurrentCursor = needMoreSize.coerceAtMost(subscriptionSlice.size)
            val subscriptionInCurrentCursor = subscriptionsInCurrentSlot.content.subList(0, sizeOfCurrentCursor)
            subscribers += subscriptionInCurrentCursor

            nextCursor = if (subscriptionInCurrentCursor.size > needMoreSize || currentSlotId < lastSlotId) {
                subscriptionInCurrentCursor.lastOrNull()?.key?.subscriberId
            } else {
                null
            }
        }

        return CursorResult.of(
            data = subscribers,
            cursor = Cursor.of(cursor = nextCursor)
        )
    }

    suspend fun listSubscriberTargets(
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriptionResponse, String> {
        val subscriptions = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextSubscriptions(
                cursorRequest = cursorRequest,
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
            ).content

            CursorDirection.PREVIOUS -> listPreviousSubscriptions(
                cursorRequest = cursorRequest,
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId
            ).content
        }

        val data = when (cursorRequest.direction) {
            CursorDirection.NEXT ->
                subscriptions.subList(0, (cursorRequest.pageSize).coerceAtMost(subscriptions.size))

            CursorDirection.PREVIOUS ->
                subscriptions.subList(0, (cursorRequest.pageSize).coerceAtMost(subscriptions.size)).reversed()
        }

        return CursorResult.of(
            data = data.map { subscription -> SubscriptionResponse.of(subscription) },
            cursor = Cursor.of(
                cursor = getNextCursor(subscriptions = subscriptions, cursorRequest = cursorRequest),
            )
        )
    }

    private suspend fun listNextSubscriptions(
        cursorRequest: CursorRequest,
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdAsc(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }
        return subscriptionRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
        )
    }

    private suspend fun listPreviousSubscriptions(
        cursorRequest: CursorRequest,
        workspaceId: String,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
                workspaceId = workspaceId,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
            )
        }

        return subscriptionRepository.findAllByKeyWorkspaceIdAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
            workspaceId = workspaceId,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
        )
    }

    private suspend fun getNextCursor(subscriptions: List<Subscription>, cursorRequest: CursorRequest): String? {
        if (subscriptions.size <= cursorRequest.pageSize) {
            return null
        }
        return subscriptions[cursorRequest.pageSize - 1].key.targetId
    }

}
