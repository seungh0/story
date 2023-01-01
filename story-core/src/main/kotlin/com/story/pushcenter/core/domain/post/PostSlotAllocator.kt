package com.story.pushcenter.core.domain.post

import com.story.pushcenter.core.common.enums.ServiceType
import org.springframework.stereotype.Repository

@Repository
class PostSlotAllocator {

    suspend fun allocate(serviceType: ServiceType, accountId: String): Long {
        return 1L
    }

}
