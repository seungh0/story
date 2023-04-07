package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.support.cache.CacheType
import com.story.platform.core.support.cache.Cacheable
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class SubscriptionRetriever(
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscribersCounterCoroutineRepository: SubscribersCounterCoroutineRepository,
    private val subscriptionsCounterCoroutineRepository: SubscriptionsCounterCoroutineRepository,
    private val subscriberReactiveRepository: SubscriberReactiveRepository,
    private val subscriptionReactiveRepository: SubscriptionReactiveRepository,
    private val subscriberIdGenerator: SubscriberIdGenerator,
) {

    suspend fun isSubscriber(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val primaryKey = SubscriptionPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        val subscription = subscriptionCoroutineRepository.findById(primaryKey)
        return subscription != null && subscription.isActivated()
    }

    @Cacheable(
        cacheType = CacheType.SUBSCRIBERS_COUNT,
        key = "'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId}",
    )
    suspend fun countSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ): Long {
        val primaryKey = SubscribersCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return subscribersCounterCoroutineRepository.findById(primaryKey)?.count ?: 0L
    }

    @Cacheable(
        cacheType = CacheType.SUBSCRIPTION_COUNT,
        key = "'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun countSubscriptions(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Long {
        val primaryKey = SubscriptionsCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
        )
        return subscriptionsCounterCoroutineRepository.findById(primaryKey)?.count ?: 0L
    }

    suspend fun getSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscriber, String> {
        return when (cursorRequest.direction) {
            CursorDirection.NEXT -> getSubscribersNextDirection(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> getSubscribersPreviousDirection(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        }
    }

    private suspend fun getSubscribersPreviousDirection(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscriber, String> {
        val firstSlotId = SubscriberSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriberSlotAssigner.assign(
            subscriberIdGenerator.getLastSubscriptionId(
                serviceType,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionReactiveRepository.findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: lastSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdLessThan(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
                serviceType = serviceType,
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
                cursor = Cursor(cursor = previousCursor),
            )
        }

        val subscribers = subscriptionSlice.content as MutableList<Subscriber>

        while (subscribers.size < cursorRequest.pageSize && --currentSlotId >= firstSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscriptionsInCurrentSlot =
                subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
                    serviceType = serviceType,
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
            cursor = Cursor(cursor = previousCursor)
        )
    }

    private suspend fun getSubscribersNextDirection(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscriber, String> {
        val firstSlotId = SubscriberSlotAssigner.FIRST_SLOT_ID
        val lastSlotId = SubscriberSlotAssigner.assign(
            subscriberIdGenerator.getLastSubscriptionId(
                serviceType,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionReactiveRepository.findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: firstSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
                serviceType = serviceType,
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
                cursor = Cursor(cursor = nextCursor),
            )
        }

        val subscribers = subscriptionSlice.content as MutableList<Subscriber>

        while (cursorRequest.pageSize > subscribers.size && ++currentSlotId <= lastSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscribers.size
            val subscriptionsInCurrentSlot =
                subscriberReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                    serviceType = serviceType,
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
            cursor = Cursor(cursor = nextCursor)
        )
    }

    suspend fun getSubscriptionTargets(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscription, String> {
        val subscriptions = when (cursorRequest.direction) {
            CursorDirection.NEXT -> getSubscriptionTargetsNextDirection(
                cursorRequest = cursorRequest,
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
            )

            CursorDirection.PREVIOUS -> getSubscriptionTargetsPreviousDirection(
                cursorRequest = cursorRequest,
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId
            )
        }
        return CursorResult(
            data = subscriptions.content,
            cursor = getCursor(subscriptions)
        )
    }

    private suspend fun getSubscriptionTargetsPreviousDirection(
        cursorRequest: CursorRequest,
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        return subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdOrderByKeyTargetIdDesc(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize)
        )
    }

    private suspend fun getSubscriptionTargetsNextDirection(
        cursorRequest: CursorRequest,
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<Subscription> {
        if (cursorRequest.cursor == null) {
            return subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }
        return subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThan(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize)
        )
    }

    private fun getCursor(subscriptions: Slice<Subscription>): Cursor<String> {
        if (subscriptions.hasNext()) {
            return Cursor(cursor = subscriptions.content.last().key.targetId)
        }
        return Cursor(cursor = null)
    }

}
