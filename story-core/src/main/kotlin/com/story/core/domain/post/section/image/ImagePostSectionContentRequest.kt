package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentRequest(
    override val priority: Long,
    val fileId: Long,
) : PostSectionContentRequest {

    override fun sectionType() = PostSectionType.IMAGE

}
