package com.story.api.application.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.application.post.PostMetadataResponse
import com.story.api.application.post.PostOwnerResponse
import com.story.api.application.post.PostResponse
import com.story.api.application.post.PostSectionResponse
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.feed.FeedId
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.image.ImagePostSectionContent
import com.story.core.domain.post.section.link.LinkPostSectionContent
import com.story.core.domain.post.section.text.TextPostSectionContent
import com.story.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.UUID

@DocsTest
@ApiTest(FeedRetrieveApi::class)
class FeedRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedRetrieveHandler: FeedRetrieveHandler,
) : StringSpecDocsTest({

    "피드를 조회합니다 (PostWithSections)" {
        // given
        val componentId = "timeline"
        val subscriberId = "user-100"

        val cursor = UUID.randomUUID().toString()
        val direction = CursorDirection.NEXT
        val pageSize = 30

        coEvery {
            feedRetrieveHandler.listFeeds(
                workspaceId = any(),
                componentId = componentId,
                ownerId = subscriberId,
                request = any(),
            )
        } returns FeedListResponse(
            feeds = listOf(
                FeedResponse(
                    feedId = FeedId(
                        itemResourceId = ResourceId.POSTS,
                        itemComponentId = "user-post",
                        itemId = "10000",
                    ).makeKey(),
                    itemResourceId = ResourceId.POSTS.code,
                    itemComponentId = "user-post",
                    item = PostResponse(
                        postId = "10000",
                        title = "스토리 플랫폼(Story Platform)이란?",
                        sections = listOf(
                            PostSectionResponse(
                                sectionType = PostSectionType.TEXT,
                                data = TextPostSectionContent(
                                    content = "섹션 내용",
                                    extra = emptyMap(),
                                )
                            )
                        ),
                        owner = PostOwnerResponse(
                            ownerId = "user-1",
                            isOwner = false,
                        ),
                        metadata = PostMetadataResponse(
                            hasChildren = false,
                        ),
                        parentId = null,
                        depth = 1,
                        extra = emptyMap(),
                    ).apply {
                        this.createdAt = LocalDateTime.now()
                        this.updatedAt = LocalDateTime.now()
                    }
                ),
                FeedResponse(
                    feedId = FeedId(
                        itemResourceId = ResourceId.POSTS,
                        itemComponentId = "user-post",
                        itemId = "10001",
                    ).makeKey(),
                    itemResourceId = ResourceId.POSTS.code,
                    itemComponentId = "user-post",
                    item = PostResponse(
                        postId = "10001",
                        title = "스토리 플랫폼(Story Platform)이란?",
                        sections = listOf(
                            PostSectionResponse(
                                sectionType = PostSectionType.IMAGE,
                                data = ImagePostSectionContent(
                                    path = "/store/v1/store.png",
                                    width = 480,
                                    height = 360,
                                    domain = "https://localhost",
                                    extra = emptyMap(),
                                )
                            )
                        ),
                        owner = PostOwnerResponse(
                            ownerId = "user-10",
                            isOwner = false,
                        ),
                        metadata = PostMetadataResponse(
                            hasChildren = false,
                        ),
                        parentId = null,
                        depth = 1,
                        extra = emptyMap(),
                    ).apply {
                        this.createdAt = LocalDateTime.now()
                        this.updatedAt = LocalDateTime.now()
                    }
                ),
                FeedResponse(
                    feedId = FeedId(
                        itemResourceId = ResourceId.POSTS,
                        itemComponentId = "user-post",
                        itemId = "10002",
                    ).makeKey(),
                    itemResourceId = ResourceId.POSTS.code,
                    itemComponentId = "user-post",
                    item = PostResponse(
                        postId = "10002",
                        title = "스토리 플랫폼(Story Platform)이란?",
                        sections = listOf(
                            PostSectionResponse(
                                sectionType = PostSectionType.LINK,
                                data = LinkPostSectionContent(
                                    link = "https://intro.threedollars.co.kr",
                                    extra = mapOf(
                                        "og:image" to "http://localhost:5000/abc.png",
                                        "og:title" to "뽀미 토키",
                                        "og:description" to "뽀미랑 토키의 사진입니다",
                                    ),
                                )
                            )
                        ),
                        owner = PostOwnerResponse(
                            ownerId = "user-1",
                            isOwner = false,
                        ),
                        metadata = PostMetadataResponse(
                            hasChildren = false,
                        ),
                        parentId = null,
                        depth = 1,
                        extra = emptyMap(),
                    ).apply {
                        this.createdAt = LocalDateTime.now()
                        this.updatedAt = LocalDateTime.now()
                    }
                ),
            ),
            cursor = CursorResponse(
                hasNext = true,
                nextCursor = UUID.randomUUID().toString(),
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/feed-components/{componentId}/owners/{ownerId}?cursor={cursor}&direction={direction}&pageSize={pageSize}",
                componentId, subscriberId, cursor, direction, pageSize
            )
            .headers(WebClientUtils.apiKeyHeaderWithRequestUserId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "feed-post.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderWithRequestUserIdDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("ownerId").description("피드 소유자 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("페이지 커서").optional()
                            .attributes(RestDocsUtils.remarks("첫 페이지의 경우 null")),
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지 조회할 수 있습니다")),
                        RequestDocumentation.parameterWithName("direction").description("조회 방향").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(CursorDirection::class.java) + "\n(기본값: NEXT)")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.feeds")
                            .type(JsonFieldType.ARRAY).description("피드 목록"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].feedId")
                            .type(JsonFieldType.STRING).description("피드 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].itemResourceId")
                            .type(JsonFieldType.STRING).description("피드 아이템 리소스 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].itemComponentId")
                            .type(JsonFieldType.STRING).description("피드 아이템 컴포넌트 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item")
                            .type(JsonFieldType.OBJECT).description("피드로 발행된 포스트 정보"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.parentId")
                            .type(JsonFieldType.STRING).description("포스트 Parent ID").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.postId")
                            .type(JsonFieldType.STRING).description("포스트 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.depth")
                            .type(JsonFieldType.NUMBER).description("포스트가 속한 Depth"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.owner")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.owner.ownerId")
                            .type(JsonFieldType.STRING).description("포스트 작성자의 계정 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.owner.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("포스트 작성자 여부"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.metadata")
                            .type(JsonFieldType.OBJECT).description("포스트 메타 정보"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.metadata.hasChildren")
                            .type(JsonFieldType.BOOLEAN).description("포스트의 하위 포스트 존재 여부"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.title")
                            .type(JsonFieldType.STRING).description("포스트 제목"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections")
                            .type(JsonFieldType.ARRAY).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].sectionType")
                            .type(JsonFieldType.STRING).description("포스트 섹션 타입")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSectionType::class.java))),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data")
                            .type(JsonFieldType.OBJECT).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.extra")
                            .type(JsonFieldType.OBJECT).description("부가적으로 사용할 필드").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.content")
                            .type(JsonFieldType.STRING).description("[TEXT 섹션 전용] 포스트 섹션 내용").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.path")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 Path").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.domain")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 도메인").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.width")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 가로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.height")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 세로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.fileSize")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 파일 사이즈").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.link")
                            .type(JsonFieldType.STRING).description("[LINK 섹션 전용] Link").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.extra.og:image")
                            .type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (image)").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.extra.og:title")
                            .type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (title)").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.sections[].data.extra.og:description")
                            .type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (description)").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.extra")
                            .type(JsonFieldType.OBJECT).description("포스트 추가 정보").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].item.updatedAt")
                            .type(JsonFieldType.STRING).description("포스트 최근 수정 일자"),
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
