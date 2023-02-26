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
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val subscriptionReactiveRepository: SubscriptionReactiveRepository,
    private val subscriptionReverseReactiveRepository: SubscriptionReverseReactiveRepository,
    private val subscriptionIdGenerator: SubscriptionIdGenerator,
) {

    suspend fun checkSubscription(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val primaryKey = SubscriptionReversePrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        val subscription = subscriptionReverseCoroutineRepository.findById(primaryKey)
        return subscription != null && subscription.isActivated()
    }

    @Cacheable(
        cacheType = CacheType.SUBSCRIBERS_COUNT,
        key = "(T(java.lang.Math).random() * 16).intValue() + 'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId}",
    )
    suspend fun getSubscribersCount(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
    ): Long {
        val primaryKey = SubscriptionCounterPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
        )
        return subscriptionCounterCoroutineRepository.findById(primaryKey)?.count ?: 0L
    }

    suspend fun getTargetSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscription, String> {
        return when (cursorRequest.direction) {
            CursorDirection.NEXT -> getNextTargetSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )

            CursorDirection.PREVIOUS -> getPreviousTargetSubscribers(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                cursorRequest = cursorRequest,
            )
        }
    }

    private suspend fun getPreviousTargetSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscription, String> {
        val firstSlotId = SubscriptionSlotAllocator.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAllocator.allocate(
            subscriptionIdGenerator.getLastSubscriptionId(
                serviceType,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionReverseReactiveRepository.findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: lastSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdLessThan(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThan(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        var previousCursor = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
        if (previousCursor == null && currentSlotId > firstSlotId) {
            subscriptionSlice.content.lastOrNull()?.key?.subscriberId ?: cursorRequest.cursor
        }

        if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
            return CursorResult.of(
                data = subscriptionSlice.content,
                cursor = Cursor(cursor = previousCursor),
            )
        }

        val subscriptions = subscriptionSlice.content as MutableList<Subscription>

        while (subscriptions.size < cursorRequest.pageSize && --currentSlotId >= firstSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscriptions.size
            val subscriptionsInCurrentSlot =
                subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdLessThan(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    slotId = lastSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                )

            val sizeOfCurrentCursor = needMoreSize.coerceAtMost(subscriptionSlice.size)
            val subscriptionInCurrentCursor = subscriptionsInCurrentSlot.content.subList(0, sizeOfCurrentCursor)
            subscriptions += subscriptionInCurrentCursor

            previousCursor =
                if (subscriptionsInCurrentSlot.size > needMoreSize || currentSlotId > firstSlotId) {
                    subscriptionInCurrentCursor.lastOrNull()?.key?.subscriberId
                } else {
                    null
                }
        }

        return CursorResult.of(
            data = subscriptions,
            cursor = Cursor(cursor = previousCursor)
        )
    }

    private suspend fun getNextTargetSubscribers(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscription, String> {
        val firstSlotId = SubscriptionSlotAllocator.FIRST_SLOT_ID
        val lastSlotId = SubscriptionSlotAllocator.allocate(
            subscriptionIdGenerator.getLastSubscriptionId(
                serviceType,
                subscriptionType,
                targetId
            )
        )

        var currentSlotId: Long = cursorRequest.cursor?.let { cursor ->
            subscriptionReverseReactiveRepository.findByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                subscriberId = cursor,
            )?.slotId
        } ?: firstSlotId

        val subscriptionSlice = if (cursorRequest.cursor == null) {
            subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        } else {
            subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdGreaterThanEqual(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = currentSlotId,
                subscriberId = cursorRequest.cursor,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        var nextCursor: String? = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
        if (nextCursor == null && currentSlotId < lastSlotId) {
            subscriptionSlice.content.lastOrNull()?.key?.subscriberId ?: cursorRequest.cursor
        }

        if (subscriptionSlice.size >= cursorRequest.pageSize) {
            return CursorResult.of(
                data = subscriptionSlice.content,
                cursor = Cursor(cursor = nextCursor),
            )
        }

        val subscriptions = subscriptionSlice.content as MutableList<Subscription>

        while (cursorRequest.pageSize > subscriptions.size && ++currentSlotId <= lastSlotId) {
            val needMoreSize = cursorRequest.pageSize - subscriptions.size
            val subscriptionsInCurrentSlot =
                subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotId(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId,
                    slotId = currentSlotId,
                    pageable = CassandraPageRequest.first(needMoreSize + 1)
                )

            val sizeOfCurrentCursor = needMoreSize.coerceAtMost(subscriptionSlice.size)
            val subscriptionInCurrentCursor = subscriptionsInCurrentSlot.content.subList(0, sizeOfCurrentCursor)
            subscriptions += subscriptionInCurrentCursor

            nextCursor = if (subscriptionInCurrentCursor.size > needMoreSize || currentSlotId < lastSlotId) {
                subscriptionInCurrentCursor.lastOrNull()?.key?.subscriberId
            } else {
                null
            }
        }

        return CursorResult.of(
            data = subscriptions,
            cursor = Cursor(cursor = nextCursor)
        )
    }

    suspend fun getSubscriberTargets(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriptionReverse, String> {
        val subscriptions = when (cursorRequest.direction) {
            CursorDirection.NEXT -> getNextSubscriberTargets(
                cursorRequest = cursorRequest,
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
            )

            CursorDirection.PREVIOUS -> getPreviousSubscriberTargets(
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

    private suspend fun getPreviousSubscriberTargets(
        cursorRequest: CursorRequest,
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<SubscriptionReverse> {
        if (cursorRequest.cursor == null) {
            return subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }

        return subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdOrderByKeyTargetIdDesc(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize)
        )
    }

    private suspend fun getNextSubscriberTargets(
        cursorRequest: CursorRequest,
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
    ): Slice<SubscriptionReverse> {
        if (cursorRequest.cursor == null) {
            return subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberId(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize)
            )
        }
        return subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThan(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = cursorRequest.cursor,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize)
        )
    }

    private fun getCursor(subscriptions: Slice<SubscriptionReverse>): Cursor<String> {
        if (subscriptions.hasNext()) {
            return Cursor(cursor = subscriptions.content.last().key.targetId)
        }
        return Cursor(cursor = null)
    }

}
