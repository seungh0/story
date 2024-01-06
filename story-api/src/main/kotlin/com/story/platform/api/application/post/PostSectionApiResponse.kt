package com.story.platform.api.application.post

import com.story.platform.core.domain.post.section.PostSectionContentResponse
import com.story.platform.core.domain.post.section.PostSectionType

data class PostSectionApiResponse(
    val sectionType: PostSectionType,
    val data: PostSectionContentResponse,
)
