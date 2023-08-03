package com.story.platform.api.domain.subscription

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(SubscriptionRetrieveApi::class)
class SubscriptionRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val subscriptionRetrieveHandler: SubscriptionRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )
    }

    "대상을 구독했는지 여부를 확인합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"
        val targetId = "targetId"

        coEvery {
            subscriptionRetrieveHandler.isSubscriber(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
            )
        } returns SubscriptionCheckApiResponse(
            isSubscriber = true,
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}",
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
                    "SUBSCRIPTION-IS-SUBSCRIBE-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId").description("Subscriber Id"),
                        RequestDocumentation.parameterWithName("targetId").description("Subscription Target Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.isSubscriber")
                            .type(JsonFieldType.BOOLEAN).description("isSubscriber (true/false)"),
                    )
                )
            )
    }

    "구독자 수를 확인합니다" {
        // given
        val componentId = "follow"
        val targetId = "targetId"

        coEvery {
            subscriptionRetrieveHandler.countSubscribers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
            )
        } returns SubscribersCountApiResponse(
            subscribersCount = 13500L
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/subscriptions/components/{componentId}/targets/{targetId}/subscribers/count",
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
                    "SUBSCRIPTION-SUBSCRIBER-COUNT-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("targetId").description("Subscription Target Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.subscribersCount")
                            .type(JsonFieldType.NUMBER).description("Subscribers Count"),
                    )
                )
            )
    }

    "구독한 대상 수를 조회합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"

        coEvery {
            subscriptionRetrieveHandler.countSubscriptions(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
            )
        } returns SubscriptionsCountApiResponse(
            subscriptionsCount = 350,
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/subscriptions/components/{componentId}/subscribers/{subscriberId}/subscriptions/count",
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
                    "SUBSCRIPTION-SUBSCRIPTION-TARGET-COUNT-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId")
                            .description("Subscription Subscriber Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.subscriptionsCount")
                            .type(JsonFieldType.NUMBER).description("Subscription Targets Count"),
                    )
                )
            )
    }

    "구독자 목록을 조회합니다" {
        // given
        val componentId = "follow"
        val targetId = "targetId"

        val request = CursorRequest(
            pageSize = 30,
            direction = CursorDirection.NEXT,
            cursor = "cursor",
        )

        coEvery {
            subscriptionRetrieveHandler.listTargetSubscribers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                cursorRequest = request,
            )
        } returns CursorResult(
            data = listOf(
                SubscriberApiResponse(
                    subscriberId = "subscriber-1",
                ),
                SubscriberApiResponse(
                    subscriberId = "subscriber-2",
                ),
            ),
            cursor = Cursor(
                nextCursor = "nextCursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/subscriptions/components/{componentId}/targets/{targetId}/subscribers?cursor=${request.cursor}&pageSize=${request.pageSize}&direction=${request.direction}",
                componentId, targetId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "SUBSCRIPTION-SUBSCRIBER-LIST-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("targetId")
                            .description("Subscription Target Id"),
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
                            .type(JsonFieldType.ARRAY).description("subscriber list"),
                        PayloadDocumentation.fieldWithPath("result.data[].subscriberId")
                            .type(JsonFieldType.STRING).description("SubscriberID Id"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("nextCursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("hasNext"),
                    )
                )
            )
    }

    "구독중인 대상자 목록을 조회합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriberId"

        val request = CursorRequest(
            pageSize = 30,
            direction = CursorDirection.NEXT,
            cursor = "cursor",
        )

        coEvery {
            subscriptionRetrieveHandler.listSubscriberTargets(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = request,
            )
        } returns CursorResult(
            data = listOf(
                SubscriptionTargetApiResponse(
                    targetId = "target-1",
                    alarm = true,
                ),
                SubscriptionTargetApiResponse(
                    targetId = "target-2",
                    alarm = true,
                ),
                SubscriptionTargetApiResponse(
                    targetId = "target-3",
                    alarm = false,
                )
            ),
            cursor = Cursor(
                nextCursor = "nextCursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets?cursor=${request.cursor}&pageSize=${request.pageSize}&direction=${request.direction}",
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
                    "SUBSCRIPTION-SUBSCRIPTION-TARGET-LIST-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId")
                            .description("Subscription Subscriber Id"),
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
                            .type(JsonFieldType.ARRAY).description("subscription target list"),
                        PayloadDocumentation.fieldWithPath("result.data[].targetId")
                            .type(JsonFieldType.STRING).description("Target Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].alarm")
                            .type(JsonFieldType.BOOLEAN).description("alarm ON/OFF (true/false)"),
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
