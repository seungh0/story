package com.story.platform.core.domain.post

import com.story.platform.core.common.utils.SlotAllocator

object PostSlotAllocator {

    fun allocate(
        postId: Long,
    ) = SlotAllocator.allocate(
        id = postId,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    private const val SLOT_SIZE = 15_000
    const val FIRST_SLOT_ID = 1L

}
