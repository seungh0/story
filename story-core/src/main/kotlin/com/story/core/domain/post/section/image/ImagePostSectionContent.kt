package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionType

data class ImagePostSectionContent(
    val path: String, // /store/v1
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String, // flower.png
    val extra: Map<String, Any>,
) : PostSectionContent {

    override fun sectionType(): PostSectionType = PostSectionType.IMAGE

}
