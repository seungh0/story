package com.story.platform.core.common.utils

object SlotAllocator {

    fun allocate(
        id: Long,
        firstSlotId: Long,
        slotSize: Int,
    ): Long {
        check(value = id >= MIN_ID, lazyMessage = { "id($id)는 ${MIN_ID}보다 커야합니다" })
        return ((id - MIN_ID) / slotSize) + firstSlotId
    }

    private const val MIN_ID = 1L

}
