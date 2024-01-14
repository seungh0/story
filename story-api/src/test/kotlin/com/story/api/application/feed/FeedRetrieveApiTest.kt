package com.story.api.application.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.application.post.PostApiResponse
import com.story.api.application.post.PostMetadataApiResponse
import com.story.api.application.post.PostOwnerApiResponse
import com.story.api.application.post.PostSectionApiResponse
import com.story.api.application.subscription.SubscriptionApiResponse
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.post.section.PostSectionType
import com.story.core.domain.post.section.image.ImagePostSectionContentResponse
import com.story.core.domain.post.section.text.TextPostSectionContentResponse
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

    "피드를 조회합니다 (Post)" {
        // given
        val componentId = "timeline"
        val subscriberId = "user-100"

        val cursor = UUID.randomUUID().toString()
        val direction = CursorDirection.NEXT
        val pageSize = 30

        coEvery {
            feedRetrieveHandler.listFeeds(
                workspaceId = any(),
                feedComponentId = componentId,
                subscriberId = subscriberId,
                request = any(),
            )
        } returns FeedListApiResponse(
            feeds = listOf(
                FeedApiResponse(
                    feedId = "30000",
                    resourceId = ResourceId.POSTS.code,
                    componentId = "user-post",
                    payload = PostApiResponse(
                        postId = "1000",
                        title = "스토리 플랫폼(Story Platform)이란?",
                        sections = listOf(
                            PostSectionApiResponse(
                                sectionType = PostSectionType.TEXT,
                                data = TextPostSectionContentResponse(
                                    content = "섹션 내용"
                                )
                            )
                        ),
                        owner = PostOwnerApiResponse(
                            ownerId = "user-1",
                            isOwner = false,
                        ),
                        metadata = PostMetadataApiResponse(
                            hasChildren = false,
                        )
                    ).apply {
                        this.createdAt = LocalDateTime.now()
                        this.updatedAt = LocalDateTime.now()
                    }
                ),
                FeedApiResponse(
                    feedId = "30000",
                    resourceId = ResourceId.POSTS.code,
                    componentId = "user-post",
                    payload = PostApiResponse(
                        postId = "1000",
                        title = "스토리 플랫폼(Story Platform)이란?",
                        sections = listOf(
                            PostSectionApiResponse(
                                sectionType = PostSectionType.IMAGE,
                                data = ImagePostSectionContentResponse(
                                    path = "/store/v1/store.png",
                                    fileName = "store.png",
                                    width = 480,
                                    height = 360,
                                    fileSize = 1234123,
                                )
                            )
                        ),
                        owner = PostOwnerApiResponse(
                            ownerId = "user-10",
                            isOwner = false,
                        ),
                        metadata = PostMetadataApiResponse(
                            hasChildren = false,
                        )
                    ).apply {
                        this.createdAt = LocalDateTime.now()
                        this.updatedAt = LocalDateTime.now()
                    }
                )
            ),
            cursor = CursorResponse(
                hasNext = true,
                nextCursor = UUID.randomUUID().toString(),
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/feeds/components/{componentId}/subscribers/{subscriberId}?cursor={cursor}&direction={direction}&pageSize={pageSize}",
                componentId, subscriberId, cursor, direction, pageSize
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestUserId)
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
                    RestDocsUtils.authenticationHeaderWithRequestUserIdDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("피드 구독자 ID"),
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
                        PayloadDocumentation.fieldWithPath("result.feeds[].resourceId")
                            .type(JsonFieldType.STRING).description("리소스 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].componentId")
                            .type(JsonFieldType.STRING).description("컴포넌트 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload")
                            .type(JsonFieldType.OBJECT).description("피드로 발행된 포스트 정보"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.postId")
                            .type(JsonFieldType.STRING).description("포스트 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.owner")
                            .type(JsonFieldType.OBJECT).description("포스트 작성자"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.owner.ownerId")
                            .type(JsonFieldType.STRING).description("포스트 작성자의 계정 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.owner.isOwner")
                            .type(JsonFieldType.BOOLEAN).description("포스트 작성자 여부"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.metadata")
                            .type(JsonFieldType.OBJECT).description("포스트 메타 정보"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.metadata.hasChildren")
                            .type(JsonFieldType.BOOLEAN).description("포스트의 하위 포스트 존재 여부"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.title")
                            .type(JsonFieldType.STRING).description("포스트 제목"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections")
                            .type(JsonFieldType.ARRAY).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].sectionType")
                            .type(JsonFieldType.STRING).description("포스트 섹션 타입")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(PostSectionType::class.java))),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data")
                            .type(JsonFieldType.OBJECT).description("포스트 섹션 목록"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.content")
                            .type(JsonFieldType.STRING).description("[TEXT 섹션 전용] 포스트 섹션 내용").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.path")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 Path").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.fileName")
                            .type(JsonFieldType.STRING).description("[IMAGE 섹션 전용] 이미지 파일 명").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.width")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 가로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.height")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 세로 길이").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.sections[].data.fileSize")
                            .type(JsonFieldType.NUMBER).description("[IMAGE 섹션 전용] 이미지 파일 사이즈").optional(),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.createdAt")
                            .type(JsonFieldType.STRING).description("포스트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.updatedAt")
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

    "피드를 조회합니다 (Subscription)" {
        // given
        val componentId = "timeline"
        val subscriberId = "subscriberId"

        coEvery {
            feedRetrieveHandler.listFeeds(
                workspaceId = any(),
                feedComponentId = componentId,
                subscriberId = subscriberId,
                request = any(),
            )
        } returns FeedListApiResponse(
            feeds = listOf(
                FeedApiResponse(
                    feedId = "30000",
                    resourceId = ResourceId.POSTS.code,
                    componentId = "user-post",
                    payload = SubscriptionApiResponse(
                        targetId = "target-id",
                        subscriberId = "subscriber-id"
                    )
                )
            ),
            cursor = CursorResponse(
                hasNext = true,
                nextCursor = UUID.randomUUID().toString(),
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/feeds/components/{componentId}/subscribers/{subscriberId}?cursor=cursor&direction=NEXT&pageSize=30",
                componentId, subscriberId
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "feed-subscription.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("피드 구독자 ID"),
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
                        PayloadDocumentation.fieldWithPath("result.feeds[].resourceId")
                            .type(JsonFieldType.STRING).description("리소스 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].componentId")
                            .type(JsonFieldType.STRING).description("컴포넌트 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload")
                            .type(JsonFieldType.OBJECT).description("피드로 발행된 구독 정보"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.subscriberId")
                            .type(JsonFieldType.STRING).description("구독자 ID"),
                        PayloadDocumentation.fieldWithPath("result.feeds[].payload.targetId")
                            .type(JsonFieldType.STRING).description("구독 대상 ID"),
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
