package com.story.platform.core.domain.resource

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.NotFoundException

/**
 * 매퍼 추가
 */
enum class ResourceId(
    val code: String,
    val description: String,
) {

    SUBSCRIPTIONS(code = "subscriptions", description = "구독"),
    POSTS(code = "posts", description = "포스팅"),
    FEEDS(code = "feeds", description = "피드"),
    ;

    companion object {
        private val cachedResourceIdMap = mutableMapOf<String, ResourceId>()

        init {
            values().forEach { resourceId -> cachedResourceIdMap[resourceId.code.lowercase()] = resourceId }
        }

        fun findByCode(code: String): ResourceId {
            return cachedResourceIdMap[code.lowercase()]
                ?: throw NotFoundException(
                    message = "해당하는 리소스($code)는 존재하지 않습니다",
                    errorCode = ErrorCode.E404_NOT_FOUND_RESOURCE,
                )
        }
    }

}
