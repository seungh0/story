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
import java.util.UUID

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
        val title = "플랫폼 정보"
        val content = """
                   1. 스토리 플랫폼(Story Platform)이란?
                   - "스토리 플랫폼"은 비즈니스를 위한 서비스를 쉽게 개발할 수 있도록 컴포넌트 플랫폼을 제공합니다.

                   2. 누구를 위한 플랫폼 인가요?
                   - 제품을 신속하게 출시하고 시장 응답을 확인하고자 하는 스타트업 또는 팀.
                   - 서버 개발과 관리의 비용과 수고를 들이지 않고 비즈니스를 위한 서비스를 만들고자 하는 개인 또는 팀.
        """.trimIndent()
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
                        RequestDocumentation.parameterWithName("componentId").description("컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("spaceId").description("포스트 공간 ID"),
                        RequestDocumentation.parameterWithName("postId").description("포스트 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.postId")
                            .type(JsonFieldType.STRING).description("포스트 ID"),
                        PayloadDocumentation.fieldWithPath("result.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 포스트 작성자 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-Account-Id 헤더를 기준으로 합니다")),
                        PayloadDocumentation.fieldWithPath("result.title")
                            .type(JsonFieldType.STRING).description("포스트 제목"),
                        PayloadDocumentation.fieldWithPath("result.content")
                            .type(JsonFieldType.STRING).description("포스트 내용"),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("포스트 최근 수정 일자"),
                        PayloadDocumentation.fieldWithPath("result.writer")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.writer.accountId")
                            .type(JsonFieldType.STRING).description("포스트 작성자의 계정 ID"),
                    )
                )
            )
    }

    "포스트 목록을 조회합니다" {
        // given
        val componentId = "account-post"
        val title = "플랫폼 정보"
        val content = """
                   1. 스토리 플랫폼(Story Platform)이란?
                   - "스토리 플랫폼"은 비즈니스를 위한 서비스를 쉽게 개발할 수 있도록 컴포넌트 플랫폼을 제공합니다.

                   2. 누구를 위한 플랫폼 인가요?
                   - 제품을 신속하게 출시하고 시장 응답을 확인하고자 하는 스타트업 또는 팀.
                   - 서버 개발과 관리의 비용과 수고를 들이지 않고 비즈니스를 위한 서비스를 만들고자 하는 개인 또는 팀.
        """.trimIndent()
        val spaceId = "user-spaceId"
        val postId = "20000"
        val cursor = UUID.randomUUID().toString()
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
                nextCursor = UUID.randomUUID().toString(),
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
                        RequestDocumentation.parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("spaceId").description("포스트 공간 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("페이지 커서").optional()
                            .attributes(RestDocsUtils.remarks("첫 페이지의 경우 null")),
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지만 허용")),
                        RequestDocumentation.parameterWithName("direction").description("조회 방향").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(CursorDirection::class.java) + "\n(기본값: NEXT)")),
                        RequestDocumentation.parameterWithName("sortBy").description("정렬 방식").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSortBy::class.java) + "\n(default: LATEST)")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.posts")
                            .type(JsonFieldType.ARRAY).description("포스트 목록"),
                        PayloadDocumentation.fieldWithPath("result.posts[].postId")
                            .type(JsonFieldType.STRING).description("포스트 ID"),
                        PayloadDocumentation.fieldWithPath("result.posts[].isOwner")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 포스트 작성자 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-Account-Id 헤더를 기준으로 합니다")),
                        PayloadDocumentation.fieldWithPath("result.posts[].title")
                            .type(JsonFieldType.STRING).description("포스트 제목")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.posts[].content")
                            .type(JsonFieldType.STRING).description("포스트 내용"),
                        PayloadDocumentation.fieldWithPath("result.posts[].createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].updatedAt")
                            .type(JsonFieldType.STRING).description("포스트 최근 수정 일자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].writer")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].writer.accountId")
                            .type(JsonFieldType.STRING).description("포스트 작성자의 계정 ID"),
                        PayloadDocumentation.fieldWithPath("result.cursor")
                            .type(JsonFieldType.OBJECT).description("페이지 커서 정보"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .type(JsonFieldType.STRING).description("다음 페이지를 조회하기 위한 커서")
                            .attributes(RestDocsUtils.remarks("다음 페이지가 없는 경우 null"))
                            .optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("다음 페이지의 존재 여부)"),
                    )
                )
            )
    }

})
