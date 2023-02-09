package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import org.springframework.data.cassandra.core.query.CassandraPageRequest
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
        subscriptionType: String,
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

    suspend fun getSubscribersCount(
        serviceType: ServiceType,
        subscriptionType: String,
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
        subscriptionType: String,
        targetId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<Subscription, String> {
        when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
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
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdGreaterThan(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = currentSlotId,
                        pageable = CassandraPageRequest.first(cursorRequest.pageSize)
                    )
                } else {
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdGreaterThan(
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
                        subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdGreaterThan(
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
            CursorDirection.PREVIOUS -> {
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
        }
    }

    suspend fun getSubscriberTargets(
        serviceType: ServiceType,
        subscriptionType: String,
        subscriberId: String,
        cursorRequest: CursorRequest,
    ): CursorResult<SubscriptionReverse, String> {
        when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                val subscriptionSlice = if (cursorRequest.cursor == null) {
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdGreaterThanEqual(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
                    )
                } else {
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanEqual(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor,
                        pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1)
                    )
                }

                return CursorResult.of(
                    data = subscriptionSlice.content,
                    cursor = Cursor(
                        cursor = SubscriptionCursorCalculator.getNextCursorBySubscriptionReverse(
                            subscriptionSlice
                        )
                    )
                )
            }
            CursorDirection.PREVIOUS -> {
                val subscriptionSlice =
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanEqual(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor!!,
                        pageable = CassandraPageRequest.first(cursorRequest.pageSize)
                    )

                return CursorResult.of(
                    data = subscriptionSlice.content,
                    cursor = Cursor(
                        cursor = SubscriptionCursorCalculator.getNextCursorBySubscriptionReverse(
                            subscriptionSlice
                        )
                    )
                )
            }
        }
    }

}
