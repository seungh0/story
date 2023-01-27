package com.story.platform.core.domain.subscription

object SubscriptionSlotAllocator {

    fun allocate(
        subscriptionSequence: Long,
    ) = (subscriptionSequence / SLOT_SIZE) + FIRST_SLOT_ID

    private const val SLOT_SIZE = 50_000
    const val FIRST_SLOT_ID = 1L

}
