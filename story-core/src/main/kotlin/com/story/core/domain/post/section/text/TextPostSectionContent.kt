package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionType

data class TextPostSectionContent(
    val content: String,
) : PostSectionContent {

    override fun sectionType(): PostSectionType = PostSectionType.TEXT

}
