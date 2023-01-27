package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.stereotype.Repository

@Repository
class PostSlotAllocator(
    private val postSequenceGenerator: PostSequenceGenerator,
) {

    suspend fun getCurrentSlot(
        serviceType: ServiceType,
        accountId: String,
        spaceType: String,
        spaceId: String,
    ): Long {
        return getSlotBySequence(
            sequence = postSequenceGenerator.getLastSequence(
                serviceType = serviceType,
                accountId = accountId,
                spaceType = spaceType,
                spaceId = spaceId,
            )
        )
    }

    suspend fun allocate(
        serviceType: ServiceType,
        accountId: String,
        spaceType: String,
        spaceId: String,
    ): Long {
        return getSlotBySequence(
            sequence = postSequenceGenerator.generate(
                serviceType = serviceType,
                accountId = accountId,
                spaceType = spaceType,
                spaceId = spaceId,
            )
        )
    }

    private fun getSlotBySequence(sequence: Long): Long {
        return sequence / SLOT_SIZE + FIRST_SLOT_ID
    }

    companion object {
        const val FIRST_SLOT_ID = 1L
        private const val SLOT_SIZE = 10_000
    }

}
