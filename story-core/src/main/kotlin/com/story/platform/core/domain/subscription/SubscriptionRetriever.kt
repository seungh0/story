package com.story.platform.core.domain.subscription

import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class SubscriptionRetriever(
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val subscriptionReactiveRepository: SubscriptionReactiveRepository,
    private val subscriptionReverseReactiveRepository: SubscriptionReverseReactiveRepository,
    private val subscriptionSlotAllocator: SubscriptionSlotAllocator,
) {

    suspend fun checkSubscription(
        serviceType: com.story.platform.core.common.enums.ServiceType,
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
        serviceType: com.story.platform.core.common.enums.ServiceType,
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
        serviceType: com.story.platform.core.common.enums.ServiceType,
        subscriptionType: String,
        targetId: String,
        cursorRequest: com.story.platform.core.common.model.CursorRequest,
    ): com.story.platform.core.common.model.CursorResult<SubscriptionResponse, String> {
        when (cursorRequest.direction) {
            com.story.platform.core.common.enums.CursorDirection.NEXT -> {
                var lastSlotNo = subscriptionSlotAllocator.getCurrentSlot(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
                val subscriptionSlice = if (cursorRequest.cursor == null) {
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdLessThan(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotNo = lastSlotNo,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                    )
                } else {
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdLessThan(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotNo = lastSlotNo,
                        subscriberId = cursorRequest.cursor,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
                    )
                }

                var nextCursor: String? = getNextCursorBySubscription(subscriptionSlice)
                if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
                    return com.story.platform.core.common.model.CursorResult.of(
                        data = subscriptionSlice.content.map { subscription -> SubscriptionResponse.of(subscription) },
                        cursor = com.story.platform.core.common.model.Cursor(cursor = nextCursor),
                    )
                }

                val subscriptions = subscriptionSlice.content as MutableList<Subscription>

                while (++lastSlotNo <= SubscriptionSlotAllocator.FIRST_SLOT_ID && subscriptions.size < cursorRequest.pageSize) {
                    val subscriptionsInSlot =
                        subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdLessThan(
                            serviceType = serviceType,
                            subscriptionType = subscriptionType,
                            targetId = targetId,
                            slotNo = lastSlotNo,
                            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                        )
                    subscriptions.addAll(subscriptionSlice.content)
                    nextCursor = getNextCursorBySubscription(subscriptionsInSlot)
                }

                return com.story.platform.core.common.model.CursorResult.of(
                    data = subscriptions.map { subscription -> SubscriptionResponse.of(subscription) },
                    cursor = com.story.platform.core.common.model.Cursor(cursor = nextCursor)
                )
            }
            com.story.platform.core.common.enums.CursorDirection.PREVIOUS -> {
                var lastSlotNo = subscriptionSlotAllocator.getCurrentSlot(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
                val subscriptionSlice =
                    subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdAndKeySubscriberIdGreaterThanOrderByKeySubscriberIdAsc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotNo = lastSlotNo,
                        subscriberId = cursorRequest.cursor!!,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
                    )

                var nextCursor: String? = getNextCursorBySubscription(subscriptionSlice)
                if (!subscriptionSlice.hasNext() && subscriptionSlice.size >= cursorRequest.pageSize) {
                    return com.story.platform.core.common.model.CursorResult.of(
                        data = subscriptionSlice.content.map { subscription -> SubscriptionResponse.of(subscription) },
                        cursor = com.story.platform.core.common.model.Cursor(cursor = nextCursor),
                    )
                }

                val subscriptions = subscriptionSlice.content as MutableList<Subscription>

                while (--lastSlotNo >= SubscriptionSlotAllocator.FIRST_SLOT_ID && subscriptions.size < cursorRequest.pageSize) {
                    val subscriptionsInSlot =
                        subscriptionReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeyTargetIdAndKeySlotNoAndKeySubscriberIdLessThan(
                            serviceType = serviceType,
                            subscriptionType = subscriptionType,
                            targetId = targetId,
                            slotNo = lastSlotNo,
                            pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                        )
                    subscriptions.addAll(subscriptionSlice.content)
                    nextCursor = getNextCursorBySubscription(subscriptionsInSlot)
                }

                return com.story.platform.core.common.model.CursorResult.of(
                    data = subscriptions.map { subscription -> SubscriptionResponse.of(subscription) },
                    cursor = com.story.platform.core.common.model.Cursor(cursor = nextCursor)
                )
            }
        }
    }

    private fun getNextCursorBySubscription(subscriptionSlice: Slice<Subscription>): String? {
        return if (subscriptionSlice.hasNext()) {
            subscriptionSlice.last().key.subscriberId
        } else {
            null
        }
    }

    suspend fun getSubscriberTargets(
        serviceType: com.story.platform.core.common.enums.ServiceType,
        subscriptionType: String,
        subscriberId: String,
        cursorRequest: com.story.platform.core.common.model.CursorRequest,
    ): com.story.platform.core.common.model.CursorResult<SubscriptionResponse, String> {
        when (cursorRequest.direction) {
            com.story.platform.core.common.enums.CursorDirection.NEXT -> {
                val subscriptionSlice = if (cursorRequest.cursor == null) {
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberId(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                    )
                } else {
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdLessThan(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize)
                    )
                }

                return com.story.platform.core.common.model.CursorResult.of(
                    data = subscriptionSlice.content.map { subscriptionReverse ->
                        SubscriptionResponse.of(
                            subscriptionReverse
                        )
                    },
                    cursor = com.story.platform.core.common.model.Cursor(
                        cursor = getNextCursorBySubscriptionReverse(
                            subscriptionSlice
                        )
                    )
                )
            }
            com.story.platform.core.common.enums.CursorDirection.PREVIOUS -> {
                val subscriptionSlice =
                    subscriptionReverseReactiveRepository.findAllByKeyServiceTypeAndKeySubscriptionTypeAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        subscriberId = subscriberId,
                        targetId = cursorRequest.cursor!!,
                        pageable = CassandraPageRequest.of(0, cursorRequest.pageSize + 1)
                    )

                return com.story.platform.core.common.model.CursorResult.of(
                    data = subscriptionSlice.content.map { subscriptionReverse ->
                        SubscriptionResponse.of(
                            subscriptionReverse
                        )
                    },
                    cursor = com.story.platform.core.common.model.Cursor(
                        cursor = getNextCursorBySubscriptionReverse(
                            subscriptionSlice
                        )
                    )
                )
            }
        }
    }

    private fun getNextCursorBySubscriptionReverse(subscriptionReverseSlice: Slice<SubscriptionReverse>): String? {
        return if (subscriptionReverseSlice.hasNext()) {
            subscriptionReverseSlice.last().key.subscriberId
        } else {
            null
        }
    }

}
