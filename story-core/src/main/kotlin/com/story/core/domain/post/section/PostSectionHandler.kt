package com.story.core.domain.post.section

interface PostSectionHandler {

    fun sectionType(): PostSectionType

    fun makeContents(requests: Collection<PostSectionContentRequest>): Map<PostSectionContentRequest, PostSectionContent>

    fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse>

}
