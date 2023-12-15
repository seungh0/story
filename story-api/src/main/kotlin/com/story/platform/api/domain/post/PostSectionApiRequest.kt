package com.story.platform.api.domain.post

import com.story.platform.core.domain.post.section.PostSectionContentRequest
import com.story.platform.core.domain.post.section.PostSectionType
import jakarta.validation.constraints.NotBlank

data class PostSectionApiRequest(
    @field:NotBlank
    val sectionType: String = "",
    val data: Map<String, Any>,
) {

    fun toSections(): PostSectionContentRequest {
        return PostSectionType.findByCode(sectionType).toTypedRequest(data)
    }

}
