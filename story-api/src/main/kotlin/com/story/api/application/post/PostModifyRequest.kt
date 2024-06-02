package com.story.api.application.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.core.domain.post.section.PostSectionContentCommand
import jakarta.validation.constraints.Size

data class PostModifyRequest(
    @field:Size(max = 100)
    val title: String?,
    val sections: List<PostSectionRequest>?,
    val extra: Map<String, String>?,
) {

    @JsonIgnore
    fun toSections(): List<PostSectionContentCommand>? {
        if (sections == null) {
            return null
        }
        return sections.map { section -> section.toSections() }
    }

}
