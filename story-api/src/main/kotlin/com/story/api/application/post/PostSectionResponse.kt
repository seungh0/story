package com.story.api.application.post

import com.story.core.domain.post.section.PostSectionContent
import com.story.core.domain.post.section.PostSectionType

data class PostSectionResponse(
    val sectionType: PostSectionType,
    val data: PostSectionContent,
)
