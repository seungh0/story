package com.story.platform.api.domain.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.platform.core.domain.post.section.PostSectionContentRequest
import jakarta.validation.constraints.Size

data class PostModifyApiRequest(
    @field:Size(max = 100)
    val title: String?,
    val sections: List<PostSectionApiRequest>?,
) {

    @JsonIgnore
    fun toSections(): List<PostSectionContentRequest>? {
        if (sections == null) {
            return null
        }
        return sections.map { section -> section.toSections() }
    }

}
