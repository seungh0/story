package com.story.api.application.post

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.component.ComponentStatus
import com.story.core.domain.post.PostSortBy
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.image.ImagePostSectionContentResponse
import com.story.core.domain.post.section.text.TextPostSectionContentResponse
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
) : StringSpecDocsTest({

    "포스트를 조회합니다" {
        // given
        val componentId = "user-post"
        val title = "플랫폼 정보"
        val spaceId = "spaceId"
        val postId = "200000"

        val post = PostApiResponse(
            postId = postId,
            title = title,
            owner = PostOwnerApiResponse(
                isOwner = false,
                ownerId = "user-1"
            ),
            sections = listOf(
                PostSectionApiResponse(
                    sectionType = PostSectionType.TEXT,
                    data = TextPostSectionContentResponse(
                        content = ""
                    )
                ),
                PostSectionApiResponse(
                    sectionType = PostSectionType.IMAGE,
                    data = ImagePostSectionContentResponse(
                        path = "/store/v1/store.png",
                        width = 480,
                        height = 360,
                        fileSize = 1234123,
                        domain = "https://localhost"
                    )
                )
            ),
            metadata = PostMetadataApiResponse(
                hasChildren = false,
            ),
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.getPost(
                workspaceId = "story",
                componentId = componentId,
                spaceId = spaceId,
                postId = postId,
                requestUserId = any(),
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
            .headers(WebClientUtils.authenticationHeaderWithRequestUserId)
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
                    RestDocsUtils.authenticationHeaderWithRequestUserIdDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("포스트 컴포넌트 ID"),
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
                        PayloadDocumentation.fieldWithPath("result.title")
                            .type(JsonFieldType.STRING).description("포스트 제목"),
                        PayloadDocumentation.fieldWithPath("result.sections")
                            .type(JsonFieldType.ARRAY).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.sections[].sectionType")
                            .type(JsonFieldType.STRING).description("포스트 섹션 타입")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSectionType::class.java))),
                        PayloadDocumentation.fieldWithPath("result.sections[].data")
                            .type(JsonFieldType.OBJECT).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.content")
                            .type(JsonFieldType.STRING).description("[TEXT 섹션 전용] 포스트 섹션 내용").optional(),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.path")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 Path").optional(),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.domain")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 도메인").optional(),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.width")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 가로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.height")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 세로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.sections[].data.fileSize")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 파일 사이즈").optional(),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("포스트 최근 수정 일자"),
                        PayloadDocumentation.fieldWithPath("result.owner")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.owner.ownerId")
                            .type(JsonFieldType.STRING).description("포스트 작성자 ID"),
                        PayloadDocumentation.fieldWithPath("result.owner.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 포스트 작성자 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-User-Id 헤더를 기준으로 합니다")),
                        PayloadDocumentation.fieldWithPath("result.metadata.hasChildren")
                            .type(JsonFieldType.BOOLEAN).description("포스트 하위에 등록된 포스트가 존재하는 지 여부"),
                    )
                )
            )
    }

    "포스트 목록을 조회합니다" {
        // given
        val componentId = "user-post"
        val title = "플랫폼 정보"
        val spaceId = "user-spaceId"
        val postId = "20000"
        val cursor = UUID.randomUUID().toString()
        val direction = CursorDirection.NEXT
        val pageSize = 10
        val sortBy = PostSortBy.LATEST

        val post = PostApiResponse(
            postId = postId,
            title = title,
            owner = PostOwnerApiResponse(
                ownerId = "user-1",
                isOwner = false,
            ),
            sections = listOf(
                PostSectionApiResponse(
                    sectionType = PostSectionType.TEXT,
                    data = TextPostSectionContentResponse(
                        content = ""
                    )
                ),
                PostSectionApiResponse(
                    sectionType = PostSectionType.IMAGE,
                    data = ImagePostSectionContentResponse(
                        path = "/store/v1/store.png",
                        width = 480,
                        height = 360,
                        fileSize = 1234123,
                        domain = "https://localhost"
                    ),
                )
            ),
            metadata = PostMetadataApiResponse(
                hasChildren = false,
            ),
        )
        post.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        post.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            postRetrieveHandler.listPosts(
                workspaceId = "story",
                componentId = componentId,
                spaceId = spaceId,
                request = any(),
                requestUserId = any(),
            )
        } returns PostListApiResponse(
            posts = listOf(post),
            cursor = CursorResponse(
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
            .headers(WebClientUtils.authenticationHeaderWithRequestUserId)
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
                    RestDocsUtils.authenticationHeaderWithRequestUserIdDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("spaceId").description("포스트 공간 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("페이지 커서").optional()
                            .attributes(RestDocsUtils.remarks("첫 페이지의 경우 null")),
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지 조회할 수 있습니다")),
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
                            .type(JsonFieldType.STRING).description("포스트 ID")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-User-Id 헤더를 기준으로 합니다")),
                        PayloadDocumentation.fieldWithPath("result.posts[].title")
                            .type(JsonFieldType.STRING).description("포스트 제목")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections")
                            .type(JsonFieldType.ARRAY).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].sectionType")
                            .type(JsonFieldType.STRING).description("포스트 섹션 타입")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSectionType::class.java))),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data")
                            .type(JsonFieldType.OBJECT).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.content")
                            .type(JsonFieldType.STRING).description("[TEXT 섹션 전용] 포스트 섹션 내용").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.path")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 Path").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.domain")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 도메인").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.width")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 가로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.height")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 세로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].sections[].data.fileSize")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 파일 사이즈").optional(),
                        PayloadDocumentation.fieldWithPath("result.posts[].createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].updatedAt")
                            .type(JsonFieldType.STRING).description("포스트 최근 수정 일자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].owner")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.posts[].owner.ownerId")
                            .type(JsonFieldType.STRING).description("포스트 작성자의 ID"),
                        PayloadDocumentation.fieldWithPath("result.posts[].owner.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 포스트 작성자 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-User-Id 헤더를 기준으로 합니다")),
                        PayloadDocumentation.fieldWithPath("result.posts[].metadata.hasChildren")
                            .type(JsonFieldType.BOOLEAN).description("포스트 하위에 등록된 포스트가 존재하는 지 여부"),
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
