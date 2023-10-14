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
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.post.PostSortBy
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
            workspaceId = "story",
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
            isOwner = false,
            writer = PostWriterApiResponse(
                accountId = "account-id"
            )
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.getPost(
                workspaceId = "story",
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                requestAccountId = any(),
            )
        } returns post

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}",
                componentId,
                spaceId,
                postId
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "post.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderWithRequestAccountIdDocumentation,
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
                        PayloadDocumentation.fieldWithPath("result.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("Is Post Owner (Request-Account-Id)"),
                        PayloadDocumentation.fieldWithPath("result.title")
                            .type(JsonFieldType.STRING).description("Post Title")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.content")
                            .type(JsonFieldType.STRING).description("Post Content"),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                        PayloadDocumentation.fieldWithPath("result.writer")
                            .type(JsonFieldType.OBJECT).description("Post Writer"),
                        PayloadDocumentation.fieldWithPath("result.writer.accountId")
                            .type(JsonFieldType.STRING).description("Post Writer AccountId"),
                    )
                )
            )
    }

    "포스트 목록을 조회합니다" {
        // given
        val componentId = "account-post"
        val title = "What is Story Platform?"
        val content = "\"Story Platform\" provides a component platform to enable you to easily develop your business services."
        val spaceId = "user-spaceId"
        val postId = "20000"
        val cursor = "current-cursor"
        val direction = CursorDirection.NEXT
        val pageSize = 10
        val sortBy = PostSortBy.LATEST

        val post = PostApiResponse(
            postId = postId,
            title = title,
            content = content,
            writer = PostWriterApiResponse(
                accountId = "account-id"
            ),
            isOwner = false,
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.listPosts(
                workspaceId = "story",
                componentId = componentId,
                spaceId = spaceId,
                cursorRequest = any(),
                request = any(),
                requestAccountId = any(),
            )
        } returns PostListApiResponse(
            posts = listOf(post),
            cursor = Cursor(
                nextCursor = "nextCursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts?cursor={cursor}&direction={direction}&pageSize={pageSize}&sortBy={sortBy}",
                componentId,
                spaceId,
                cursor,
                direction,
                pageSize,
                sortBy
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "post.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderWithRequestAccountIdDocumentation,
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
                            .attributes(RestDocsUtils.remarks("max: 50")),
                        RequestDocumentation.parameterWithName("sortBy").description("SortBy").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSortBy::class.java) + "\n(default: LATEST)")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.posts")
                            .type(JsonFieldType.ARRAY).description("post list"),
                        PayloadDocumentation.fieldWithPath("result.posts[].postId")
                            .type(JsonFieldType.STRING).description("Post Id"),
                        PayloadDocumentation.fieldWithPath("result.posts[].isOwner")
                            .type(JsonFieldType.BOOLEAN).description("Is Post Owner (Request-Account-Id)"),
                        PayloadDocumentation.fieldWithPath("result.posts[].title")
                            .type(JsonFieldType.STRING).description("Post Title")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.posts[].content")
                            .type(JsonFieldType.STRING).description("Post Content"),
                        PayloadDocumentation.fieldWithPath("result.posts[].createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.posts[].updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                        PayloadDocumentation.fieldWithPath("result.posts[].writer")
                            .type(JsonFieldType.OBJECT).description("Post Writer"),
                        PayloadDocumentation.fieldWithPath("result.posts[].writer.accountId")
                            .type(JsonFieldType.STRING).description("Post Writer AccountId"),
                        PayloadDocumentation.fieldWithPath("result.cursor")
                            .type(JsonFieldType.OBJECT).description("Page Cursor"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("Next Page Cursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("Has More Page (next direction)"),
                    )
                )
            )
    }

})
