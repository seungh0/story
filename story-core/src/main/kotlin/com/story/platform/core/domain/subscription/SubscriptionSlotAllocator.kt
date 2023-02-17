package com.story.platform.core.domain.subscription

import com.story.platform.core.common.utils.SlotAllocator

object SubscriptionSlotAllocator {

    fun allocate(
        subscriptionId: Long,
    ) = SlotAllocator.allocate(
        id = subscriptionId,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    private const val SLOT_SIZE = 30_000
    const val FIRST_SLOT_ID = 1L

}
