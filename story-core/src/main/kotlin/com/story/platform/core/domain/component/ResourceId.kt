package com.story.platform.core.domain.component

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
        // Map으로 개선
        fun findByCode(code: String): ResourceId {
            return ResourceId.values().find { resourceId -> resourceId.code == code }
                ?: throw NotFoundException("해당하는 리소스($code)는 존재하지 않습니다")
        }
    }

}
