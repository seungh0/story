package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
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
@ApiTest(PostCreateApi::class)
class PostRegisterApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postCreateHandler: PostCreateHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationKeyResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )
    }

    test("새로운 포스트를 등록한다") {
        // given
        val componentId = "post"
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
            postCreateHandler.createPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                accountId = spaceId,
                title = request.title,
                content = request.content,
                extraJson = request.extraJson,
            )
        } returns 1

        // when
        val exchange = webTestClient.post()
            .uri("/v1/posts/components/{componentId}/spaces/{spaceId}/posts", componentId, spaceId)
            .headers(WebClientUtils.authenticationHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result.postId").isEqualTo("1")
            .consumeWith(
                document(
                    "POST-CREATE-API",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("Component Id"),
                        parameterWithName("spaceId").description("Space Id")
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
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        fieldWithPath("result.postId")
                            .type(JsonFieldType.STRING).description("Post ID"),
                    )
                )
            )
    }

})
