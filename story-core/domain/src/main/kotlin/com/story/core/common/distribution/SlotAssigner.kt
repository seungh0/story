package com.story.core.common.distribution

object SlotAssigner {

    private const val MIN_ID = 1L

    fun assign(
        seq: Long,
        firstSlotId: Long,
        slotSize: Int,
    ): Long {
        require(value = seq >= MIN_ID, lazyMessage = { "id($seq)는 최소 ${MIN_ID}보다 커야합니다" })
        return ((seq - MIN_ID) / slotSize) + firstSlotId
    }

}
