package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionType

data class TextPostSectionContentResponse(
    val content: String,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.TEXT

    companion object {
        fun from(sectionContent: TextPostSectionContent) = TextPostSectionContentResponse(
            content = sectionContent.content,
        )
    }

}
