package com.story.core.domain.post.section

import com.story.core.common.distribution.SlotAssigner

object PostSectionSlotAssigner {

    fun assign(
        postId: Long,
    ) = SlotAssigner.assign(
        id = postId,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    private const val SLOT_SIZE = 100 // ContentId * 100 (컨텐츠당 최대 100개의 섹션을 가정)
    private const val FIRST_SLOT_ID = 1L

}
