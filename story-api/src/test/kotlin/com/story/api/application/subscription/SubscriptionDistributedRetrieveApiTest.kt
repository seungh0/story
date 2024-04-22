package com.story.api.application.subscription

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.common.model.CursorDirection
import com.story.core.common.model.dto.CursorResponse
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(SubscriptionDistributedRetrieveApi::class)
class SubscriptionDistributedRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val subscriptionDistributedRetrieveHandler: SubscriptionDistributedRetrieveHandler,
) : StringSpecDocsTest({

    "구독자 목록을 조회합니다 - 마커 조회" {
        // given
        val componentId = "follow"
        val targetId = "subscription-target-id"
        val parallelSize = 20

        val request = SubscriberDistributedMarkerListRequest(
            parallelSize = parallelSize,
        )

        coEvery {
            subscriptionDistributedRetrieveHandler.listSubscriberDistributedMarkers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                request = request,
            )
        } returns SubscriberDistributedMarkerListResponse(
            cursors = listOf(
                "cursor-1",
                "cursor-2",
                "cursor-3",
                "cursor-4",
                "cursor-5",
                "cursor-6",
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed-cursors?parallelSize={parallelSize}",
                componentId, targetId, parallelSize
            )
            .headers(WebClientUtils.apiKeyHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-subscriber.distributed-cursor-list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("targetId").description("구독 대상 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("parallelSize").description("분산 갯수")
                            .attributes(RestDocsUtils.remarks("최소 1개부터 최대 500개까지 조회할 수 있습니다")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.cursors")
                            .type(JsonFieldType.ARRAY).description("각 조회를 시작할 커서 목록"),
                    )
                )
            )
    }

    "구독자 목록을 조회합니다 - 분산 조회" {
        // given
        val componentId = "follow"
        val targetId = "subscription-target-id"

        val request = SubscriberListApiRequest(
            pageSize = 30,
            cursor = UUID.randomUUID().toString(),
        )

        coEvery {
            subscriptionDistributedRetrieveHandler.listSubscribersByDistributedMarkers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                request = request,
            )
        } returns SubscriberListApiResponse(
            subscribers = listOf(
                SubscriberApiResponse(
                    subscriberId = "subscriber-id-1",
                ),
                SubscriberApiResponse(
                    subscriberId = "subscriber-id-2",
                ),
            ),
            cursor = CursorResponse(
                nextCursor = "next-cursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers/distributed?cursor={cursor}&pageSize={pageSize}",
                componentId, targetId, request.cursor, request.pageSize
            )
            .headers(WebClientUtils.apiKeyHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-subscriber.distributed-list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("targetId").description("구독 대상 ID"),
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
                        PayloadDocumentation.fieldWithPath("result.subscribers")
                            .type(JsonFieldType.ARRAY).description("구독자 목록"),
                        PayloadDocumentation.fieldWithPath("result.subscribers[].subscriberId")
                            .type(JsonFieldType.STRING).description("구독자 ID"),
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
