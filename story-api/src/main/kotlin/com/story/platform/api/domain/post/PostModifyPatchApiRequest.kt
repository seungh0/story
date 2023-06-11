package com.story.platform.api.domain.post

import com.story.platform.core.common.error.BadRequestException
import jakarta.validation.constraints.NotBlank

data class PostModifyPatchApiRequest(
    @field:NotBlank
    val accountId: String = "",
    val title: String?,
    val content: String?,
    val extraJson: String? = null,
) {

    init {
        if (title == null && content == null && extraJson == null) {
            throw BadRequestException("Post Patch API 호출시 모든 필드가 null일 수 없습니다. accountId: $accountId")
        }
    }

}
