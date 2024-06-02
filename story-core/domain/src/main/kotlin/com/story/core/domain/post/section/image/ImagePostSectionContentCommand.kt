package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentCommand
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentCommand(
    override val priority: Long,
    val fileId: Long,
    val extra: Map<String, Any> = emptyMap(),
) : PostSectionContentCommand {

    override fun sectionType() = PostSectionType.IMAGE

}
