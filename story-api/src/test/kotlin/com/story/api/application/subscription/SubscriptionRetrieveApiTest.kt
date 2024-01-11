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
@ApiTest(SubscriptionRetrieveApi::class)
class SubscriptionRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val subscriptionRetrieveHandler: SubscriptionRetrieveHandler,
) : StringSpecDocsTest({

    "특정 대상을 구독했는지 여부를 확인합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"
        val targetId = "targetId"

        coEvery {
            subscriptionRetrieveHandler.existsSubscription(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns SubscriptionExistsApiResponse(
            isSubscriber = true,
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}/exists",
                componentId, subscriberId, targetId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription.exists",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("구독자 ID"),
                        RequestDocumentation.parameterWithName("targetId").description("구독 대상 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.isSubscriber")
                            .type(JsonFieldType.BOOLEAN).description("구독 여부 (true/false)"),
                    )
                )
            )
    }

    "구독자 수를 조회합니다" {
        // given
        val componentId = "follow"
        val targetId = "targetId"

        coEvery {
            subscriptionRetrieveHandler.countSubscribers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
            )
        } returns SubscriberCountApiResponse(
            subscriberCount = 13500L
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscriber-count",
                componentId, targetId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-subscriber-count.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("targetId").description("구독 대상 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.subscriberCount")
                            .type(JsonFieldType.NUMBER).description("구독자 수"),
                    )
                )
            )
    }

    "구독한 대상 수를 조회합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"

        coEvery {
            subscriptionRetrieveHandler.countTargets(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
            )
        } returns SubscriptionTargetCountApiResponse(
            targetCount = 350,
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/subscription-count",
                componentId, subscriberId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-target-count.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("구독자 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.targetCount")
                            .type(JsonFieldType.NUMBER).description("구독 대상 수"),
                    )
                )
            )
    }

    "구독자 목록을 조회합니다" {
        // given
        val componentId = "follow"
        val targetId = "subscription-target-id"

        val request = SubscriberListApiRequest(
            pageSize = 30,
            direction = CursorDirection.NEXT.name,
            cursor = UUID.randomUUID().toString(),
        )

        coEvery {
            subscriptionRetrieveHandler.listSubscribers(
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
                "/v1/resources/subscriptions/components/{componentId}/targets/{targetId}/subscribers?cursor={cursor}&pageSize={pageSize}&direction={direction}",
                componentId, targetId, request.cursor, request.pageSize, request.direction
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-subscriber.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
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

    "구독중인 대상자 목록을 조회합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"

        val request = SubscriptionTargetListApiRequest(
            pageSize = 30,
            direction = CursorDirection.NEXT.name,
            cursor = UUID.randomUUID().toString(),
        )

        coEvery {
            subscriptionRetrieveHandler.listSubscriptionTargets(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
                request = request,
            )
        } returns SubscriptionTargetListApiResponse(
            targets = listOf(
                SubscriptionTargetApiResponse(
                    targetId = "target-1",
                    alarmEnabled = true,
                ),
                SubscriptionTargetApiResponse(
                    targetId = "target-2",
                    alarmEnabled = true,
                ),
                SubscriptionTargetApiResponse(
                    targetId = "target-3",
                    alarmEnabled = false,
                )
            ),
            cursor = CursorResponse(
                nextCursor = UUID.randomUUID().toString(),
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets?cursor={cursor}&pageSize={pageSize}&direction={direction}",
                componentId, subscriberId, request.cursor, request.pageSize, request.direction
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-target.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("구독자 ID"),
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
                        PayloadDocumentation.fieldWithPath("result.targets")
                            .type(JsonFieldType.ARRAY).description("구독 대상 목록"),
                        PayloadDocumentation.fieldWithPath("result.targets[].targetId")
                            .type(JsonFieldType.STRING).description("구독 대상 ID"),
                        PayloadDocumentation.fieldWithPath("result.targets[].alarmEnabled")
                            .type(JsonFieldType.BOOLEAN).description("구독 대상에 대한 알림 설정 여부"),
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
