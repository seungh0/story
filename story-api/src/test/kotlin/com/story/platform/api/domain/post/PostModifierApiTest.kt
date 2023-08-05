package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.api.lib.isTrue
import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
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
class PostModifierApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postModifyHandler: PostModifyHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationKeyResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("기존 포스트를 수정합니다") {
        // given
        val componentId = "post"
        val postId = 10000L
        val spaceId = "accountId"

        val request = PostCreateApiRequest(
            accountId = spaceId,
            title = "Post Title",
            content = """
                    Post Content1
                    Post Content2
            """.trimIndent()
        )

        coEvery {
            postModifyHandler.patchPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                postId = postId,
                accountId = spaceId,
                title = request.title,
                content = request.content,
                extraJson = request.extraJson,
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri("/v1/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}", componentId, spaceId, postId)
            .headers(WebClientUtils.authenticationHeader)
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
                    "POST-MODIFY-API",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("Component Id"),
                        parameterWithName("spaceId").description("Space Id"),
                        parameterWithName("postId").description("Post Id")
                    ),
                    requestFields(
                        fieldWithPath("accountId").type(JsonFieldType.STRING)
                            .description("Post Owner"),
                        fieldWithPath("title").type(JsonFieldType.STRING)
                            .description("Post Title"),
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("Post content"),
                        fieldWithPath("extraJson").type(JsonFieldType.STRING)
                            .description("extra").optional(),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
