package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.component.ComponentStatus
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@DocsTest
@ApiTest(PostRetrieveApi::class)
class PostRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRetrieveHandler: PostRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "포스트를 조회합니다" {
        // given
        val componentId = "account-post"
        val title = "Post Title"
        val content = "Post Content"
        val spaceId = "spaceId"
        val postId = "200000"

        val post = PostApiResponse(
            postId = postId,
            title = title,
            content = content,
            extra = mapOf("key" to "value"),
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.getPost(
                workspaceId = "twitter",
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
            )
        } returns post

        // when
        val exchange = webTestClient.get()
            .uri("/v1/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}", componentId, spaceId, postId)
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "POST-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Component Id"),
                        RequestDocumentation.parameterWithName("spaceId").description("Post Space Id"),
                        RequestDocumentation.parameterWithName("postId").description("Post Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.postId")
                            .type(JsonFieldType.STRING).description("Post Id"),
                        PayloadDocumentation.fieldWithPath("result.title")
                            .type(JsonFieldType.STRING).description("Post Title")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.content")
                            .type(JsonFieldType.STRING).description("Post Content"),
                        PayloadDocumentation.fieldWithPath("result.extra")
                            .type(JsonFieldType.OBJECT).description("Post extra value"),
                        PayloadDocumentation.fieldWithPath("result.extra.key")
                            .type(JsonFieldType.STRING).description("Post extra value").optional(),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                    )
                )
            )
    }

    "포스트 목록을 조회합니다" {
        // given
        val componentId = "account-post"
        val title = "Post Title"
        val content = "Post Content"
        val spaceId = "spaceId"
        val postId = "20000"
        val cursor = "cursor"
        val direction = CursorDirection.NEXT
        val pageSize = 30

        val post = PostApiResponse(
            postId = postId,
            title = title,
            content = content,
            extra = mapOf("key" to "value")
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.listPosts(
                workspaceId = "twitter",
                componentId = componentId,
                spaceId = spaceId,
                cursorRequest = any(),
            )
        } returns CursorResult.of(
            data = listOf(post),
            cursor = Cursor(
                nextCursor = "nextCursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/posts/components/{componentId}/spaces/{spaceId}/posts?cursor=$cursor&direction=$direction&pageSize=$pageSize",
                componentId,
                spaceId
            )
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "POST-LIST-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Component Id"),
                        RequestDocumentation.parameterWithName("spaceId").description("Post Space Id"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("Cursor").optional()
                            .attributes(RestDocsUtils.remarks("first cursor is null")),
                        RequestDocumentation.parameterWithName("direction").description("Direction").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(CursorDirection::class.java) + "\n(default: NEXT)")),
                        RequestDocumentation.parameterWithName("pageSize").description("Page Size")
                            .attributes(RestDocsUtils.remarks("max: 30")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.data")
                            .type(JsonFieldType.ARRAY).description("post list"),
                        PayloadDocumentation.fieldWithPath("result.data[].postId")
                            .type(JsonFieldType.STRING).description("Post Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].title")
                            .type(JsonFieldType.STRING).description("Post Title")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.data[].content")
                            .type(JsonFieldType.STRING).description("Post Content"),
                        PayloadDocumentation.fieldWithPath("result.data[].extra")
                            .type(JsonFieldType.OBJECT).description("Post extra value"),
                        PayloadDocumentation.fieldWithPath("result.data[].extra.key")
                            .type(JsonFieldType.STRING).description("Post extra value").optional(),
                        PayloadDocumentation.fieldWithPath("result.data[].createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.data[].updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("nextCursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("hasNext"),
                    )
                )
            )
    }

})
