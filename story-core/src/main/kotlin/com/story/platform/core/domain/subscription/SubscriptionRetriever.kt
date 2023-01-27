package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.subscription.SubscriptionSlotAllocator.FIRST_SLOT_ID
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
        return subscriptionReverseCoroutineRepository.existsById(primaryKey)
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
                var lastSlotId = SubscriptionSlotAllocator.allocate(
                    subscriptionId = subscriptionIdGenerator.getLastSubscriptionId(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId
                    )
                )
                val subscriptionSlice = if (cursorRequest.cursor == null) {
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = lastSlotId,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                    )
                } else {
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdLessThanOrderByKeySubscriberIdDesc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = lastSlotId,
                        subscriberId = cursorRequest.cursor,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
                    )
                }

                var nextCursor: String? = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
                if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
                    return CursorResult.of(
                        data = subscriptionSlice.content,
                        cursor = Cursor(cursor = nextCursor),
                    )
                }

                val subscriptions = subscriptionSlice.content as MutableList<Subscription>

                while (++lastSlotId <= FIRST_SLOT_ID && subscriptions.size < cursorRequest.pageSize) {
                    val subscriptionsInSlot =
                        subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                            serviceType = serviceType,
                            subscriptionType = subscriptionType,
                            targetId = targetId,
                            slotId = lastSlotId,
                            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                        )
                    subscriptions.addAll(subscriptionSlice.content)
                    nextCursor = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionsInSlot)
                }

                return CursorResult.of(
                    data = subscriptions,
                    cursor = Cursor(cursor = nextCursor)
                )
            }
            CursorDirection.PREVIOUS -> {
                var lastSlot = SubscriptionSlotAllocator.allocate(
                    subscriptionId = subscriptionIdGenerator.getLastSubscriptionId(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId
                    )
                )
                val subscriptionSlice =
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdAndKeySubscriberIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = lastSlot,
                        subscriberId = cursorRequest.cursor!!,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
                    )

                var nextCursor: String? = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionSlice)
                if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
                    return CursorResult.of(
                        data = subscriptionSlice.content,
                        cursor = Cursor(cursor = nextCursor),
                    )
                }

                val subscriptions = subscriptionSlice.content as MutableList<Subscription>

                while (--lastSlot >= FIRST_SLOT_ID && subscriptions.size < cursorRequest.pageSize) {
                    val subscriptionsInSlot =
                        subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotIdOrderByKeySubscriberIdDesc(
                            serviceType = serviceType,
                            subscriptionType = subscriptionType,
                            targetId = targetId,
                            slotId = lastSlot,
                            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                        )
                    subscriptions.addAll(subscriptionSlice.content)
                    nextCursor = SubscriptionCursorCalculator.getNextCursorBySubscription(subscriptionsInSlot)
                }

                return CursorResult.of(
                    data = subscriptions,
                    cursor = Cursor(cursor = nextCursor)
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
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdOrderByKeyTargetIdDesc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                    )
                } else {
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
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
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor!!,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
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
