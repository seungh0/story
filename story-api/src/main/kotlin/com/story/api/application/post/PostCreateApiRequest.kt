package com.story.api.application.post

import com.story.core.domain.post.section.PostSectionContentRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostCreateApiRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val title: String = "",
    val sections: List<PostSectionApiRequest>,
) {

    fun toSections(): List<PostSectionContentRequest> {
        return this.sections.map { section -> section.toSections() }
    }

}
