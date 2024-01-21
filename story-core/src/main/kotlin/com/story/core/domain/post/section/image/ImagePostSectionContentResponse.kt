package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContentResponse
import com.story.core.domain.post.section.PostSectionType
import com.story.core.infrastructure.spring.SpringEnvironmentFinder

data class ImagePostSectionContentResponse(
    val domain: String,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
) : PostSectionContentResponse {

    override fun sectionType() = PostSectionType.IMAGE

    companion object {
        fun from(sectionContent: ImagePostSectionContent) = ImagePostSectionContentResponse(
            domain = SpringEnvironmentFinder.findRequiredProperty("story.photo.domain"),
            path = sectionContent.path,
            width = sectionContent.width,
            height = sectionContent.height,
            fileSize = sectionContent.fileSize,
        )
    }

}
