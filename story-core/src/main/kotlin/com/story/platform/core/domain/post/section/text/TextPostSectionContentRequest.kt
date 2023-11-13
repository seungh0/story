package com.story.platform.core.domain.post.section.text

import com.story.platform.core.domain.post.section.PostSectionContent
import com.story.platform.core.domain.post.section.PostSectionContentRequest
import com.story.platform.core.domain.post.section.PostSectionType

data class TextPostSectionContentRequest(
    override val priority: Long,
    val content: String,
) : PostSectionContentRequest {

    override fun sectionType() = PostSectionType.TEXT

    override fun toSection(): PostSectionContent = TextPostSectionContent(
        content = content,
    )

}
