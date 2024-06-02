package com.story.core.domain.post.section.link

import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionType

data class LinkPostSectionContentCommand(
    override val priority: Long,
    val link: String,
    val extra: Map<String, Any> = emptyMap(),
) : PostSectionContentCommand {

    override fun sectionType(): PostSectionType = PostSectionType.LINK

}
