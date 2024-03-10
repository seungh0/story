package com.story.api.application.post

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.WebClientUtils
import com.story.api.lib.isTrue
import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSpaceKey
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(PostRemoveApi::class)
class PostRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRemoveHandler: PostRemoveHandler,
) : FunSpecDocsTest({

    test("기존에 등록된 포스트를 삭제한다") {
        // given
        val postSpaceKey = PostSpaceKey(
            workspaceId = "story",
            componentId = "user-post",
            spaceId = "user-space-id"
        )

        val postKey = PostId(spaceId = postSpaceKey.spaceId, parentId = null, postNo = 30000L, depth = 1)

        coEvery {
            postRemoveHandler.removePost(
                postSpaceKey = postSpaceKey,
                ownerId = any(),
                postId = postKey,
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}",
                postSpaceKey.componentId,
                postSpaceKey.spaceId,
                postKey.serialize(),
            )
            .headers(WebClientUtils.apiKeyHeaderWithRequestUserId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                document(
                    "post.remove",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderWithRequestUserIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        parameterWithName("spaceId").description("포스트 공간 ID"),
                        parameterWithName("postId").description("포스트 ID"),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부")
                    )
                )
            )
    }

})
