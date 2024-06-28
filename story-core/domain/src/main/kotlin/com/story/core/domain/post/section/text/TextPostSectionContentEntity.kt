package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContentEntity
import com.story.core.domain.post.section.PostSectionType

data class TextPostSectionContentEntity(
    val content: String,
    val extra: Map<String, Any>,
) : PostSectionContentEntity {

    override fun sectionType(): PostSectionType = PostSectionType.TEXT

    fun toTextPostSectionContent() = TextPostSectionContent(
        content = this.content,
        extra = this.extra,
    )

}
