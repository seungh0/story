package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentRequest(
    override val priority: Long,
    val fileId: Long,
    val extra: Map<String, Any> = emptyMap(),
) : PostSectionContentRequest {

    override fun sectionType() = PostSectionType.IMAGE

}
