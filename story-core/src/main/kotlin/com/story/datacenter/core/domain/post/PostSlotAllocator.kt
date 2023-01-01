package com.story.datacenter.core.domain.post

import com.story.datacenter.core.common.enums.ServiceType
import org.springframework.stereotype.Repository

@Repository
class PostSlotAllocator {

    suspend fun allocate(serviceType: ServiceType, accountId: String): Long {
        return 1L
    }

}
