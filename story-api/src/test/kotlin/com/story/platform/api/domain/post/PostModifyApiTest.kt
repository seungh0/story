package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.api.lib.isTrue
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.post.PostSpaceKey
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(PostModifyApi::class)
class PostModifyApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postModifyHandler: PostModifyHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("기존 포스트를 수정합니다") {
        // given
        val componentId = "user-post"
        val postId = 7126L
        val spaceId = "user-space-id"

        val request = PostModifyApiRequest(
            title = "Post Title",
            content = """
                    Post Content1
                    Post Content2
            """.trimIndent(),
        )

        coEvery {
            postModifyHandler.patchPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "story",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                postId = postId,
                accountId = any(),
                title = request.title,
                content = request.content,
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}",
                componentId,
                spaceId,
                postId
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                document(
                    "post.modify",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("Component Id"),
                        parameterWithName("spaceId").description("Space Id"),
                        parameterWithName("postId").description("Post Id")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING)
                            .description("Post Title")
                            .attributes(remarks("must be within 100 characters")),
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("Post content")
                            .attributes(remarks("must be within 500 characters")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
