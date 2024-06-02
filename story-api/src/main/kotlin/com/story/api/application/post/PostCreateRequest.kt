package com.story.api.application.post

import com.story.core.domain.post.PostId
import com.story.core.domain.post.section.PostSectionContentCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostCreateRequest(
    val parentId: PostId?,

    @field:NotBlank
    @field:Size(max = 100)
    val title: String = "",
    val sections: List<PostSectionRequest>,
    val extra: Map<String, String> = emptyMap(),
) {

    fun toSections(): List<PostSectionContentCommand> {
        return this.sections.map { section -> section.toSections() }
    }

}
