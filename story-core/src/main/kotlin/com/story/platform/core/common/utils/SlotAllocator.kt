package com.story.platform.core.common.utils

object SlotAllocator {

    fun allocate(
        id: Long,
        firstSlotId: Long,
        slotSize: Int,
    ) = (id / slotSize) + firstSlotId

}
