package com.story.core.domain.post.section

interface PostSectionHandler {

    fun sectionType(): PostSectionType

    fun toContent(requests: Collection<PostSectionContentRequest>): Map<PostSectionContentRequest, PostSectionContent>

    fun toResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse>

}
