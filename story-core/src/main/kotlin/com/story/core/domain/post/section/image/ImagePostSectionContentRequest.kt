package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentRequest
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentRequest(
    override val priority: Long,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String,
) : PostSectionContentRequest {

    override fun sectionType() = PostSectionType.IMAGE

}
