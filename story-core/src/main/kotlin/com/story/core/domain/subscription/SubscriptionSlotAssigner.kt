package com.story.core.domain.subscription

import com.story.core.common.distribution.SlotAssigner

object SubscriptionSlotAssigner {

    fun assign(
        sequence: Long,
    ) = SlotAssigner.assign(
        seq = sequence,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    const val SLOT_SIZE = 30_000
    const val FIRST_SLOT_ID = 1L

}
