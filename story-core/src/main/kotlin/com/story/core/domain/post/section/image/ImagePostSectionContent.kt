package com.story.core.domain.post.section.image

import com.story.core.domain.post.section.PostSectionContent

data class ImagePostSectionContent(
    val path: String, // /store/v1/flower.png
    val width: Int,
    val height: Int,
    val fileSize: Long,
    val fileName: String, // flower.png
    val imageType: String, // PNG
) : PostSectionContent
