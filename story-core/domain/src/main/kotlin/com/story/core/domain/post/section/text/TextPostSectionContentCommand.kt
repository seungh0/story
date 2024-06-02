package com.story.core.domain.post.section.text

import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionType

data class TextPostSectionContentCommand(
    override val priority: Long,
    val content: String,
    val extra: Map<String, Any> = emptyMap(),
) : PostSectionContentCommand {

    override fun sectionType() = PostSectionType.TEXT

}
