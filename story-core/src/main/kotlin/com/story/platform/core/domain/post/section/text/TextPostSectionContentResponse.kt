package com.story.platform.core.domain.post.section.text

import com.story.platform.core.domain.post.section.PostSectionContentResponse
import com.story.platform.core.domain.post.section.PostSectionType

data class TextPostSectionContentResponse(
    val content: String,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.TEXT

}
