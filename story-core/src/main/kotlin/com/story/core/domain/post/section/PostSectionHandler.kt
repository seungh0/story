package com.story.core.domain.post.section

interface PostSectionHandler {

    fun sectionType(): PostSectionType

    suspend fun makeContents(
        workspaceId: String,
        requests: Collection<PostSectionContentRequest>,
    ): Map<PostSectionContentRequest, PostSectionContent>

    suspend fun makeContentResponse(contents: Collection<PostSectionContent>): Map<PostSectionContent, PostSectionContentResponse>

}
