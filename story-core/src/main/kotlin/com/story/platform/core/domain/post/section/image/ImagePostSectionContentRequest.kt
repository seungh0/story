package com.story.platform.core.domain.post.section.image

import com.story.platform.core.domain.post.section.PostSectionContent
import com.story.platform.core.domain.post.section.PostSectionContentRequest
import com.story.platform.core.domain.post.section.PostSectionType

data class ImagePostSectionContentRequest(
    override val priority: Long,
    val path: String,
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String,
) : PostSectionContentRequest {

    override fun sectionType() = PostSectionType.IMAGE

    override fun toSection(): PostSectionContent = ImagePostSectionContent(
        path = path,
        width = width,
        height = height,
        fileSize = fileSize,
        fileName = fileName,
        imageType = "PNG" // 파싱
    )

}
