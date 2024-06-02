package com.story.api.application.post

import com.story.core.common.error.InvalidArgumentsException
import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionType
import jakarta.validation.constraints.NotBlank

data class PostSectionRequest(
    @field:NotBlank
    val sectionType: String = "",
    val data: Map<String, Any>,
) {

    init {
        if (data["priority"] == null) {
            throw InvalidArgumentsException(
                message = "섹션의 priority는 필수입니다",
                reasons = listOf("section priority is missing"),
            )
        }
    }

    fun toSections(): PostSectionContentCommand {
        return PostSectionType.findByCode(sectionType).toTypedRequest(data)
    }

}
