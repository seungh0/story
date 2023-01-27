package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import org.springframework.stereotype.Repository

@Repository
// TODO: 변경
class PostSlotAllocator {

    suspend fun allocate(serviceType: ServiceType, accountId: String): Long {
        return 1L
    }

}
