package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContentResponse(
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.IMAGE

}
