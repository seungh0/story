package com.story.core.domain.post

import com.story.core.common.distribution.SlotAssigner

/**
 * 300byte * 10_000 = 3MB
 * - 한 파티션을 약 3MB로 유지하기 위한 슬롯 정책
 */
object PostSlotAssigner {

    fun assign(
        postNo: Long,
    ) = SlotAssigner.assign(
        seq = postNo,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    private const val SLOT_SIZE = 10_000
    const val FIRST_SLOT_ID = 1L

}
