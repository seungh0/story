package com.story.platform.api.domain.subscription

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
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
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
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("targetId").description("Subscription Target Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.subscriberCount")
                            .type(JsonFieldType.NUMBER).description("Subscriber Count"),
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
                        PayloadDocumentation.fieldWithPath("result.targetCount")
                            .type(JsonFieldType.NUMBER).description("Subscription Target Count"),
                    )
                )
            )
    }

    "구독자 목록을 조회합니다" {
        // given
        val componentId = "follow"
        val targetId = "subscription-target-id"

        val request = CursorRequest(
            pageSize = 30,
            direction = CursorDirection.NEXT,
            cursor = "current-cursor",
        )

        coEvery {
            subscriptionRetrieveHandler.listSubscribers(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                cursorRequest = request,
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
            cursor = Cursor(
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
        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription-subscriber.list",
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
                            .attributes(RestDocsUtils.remarks("max: 50")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.subscribers")
                            .type(JsonFieldType.ARRAY).description("subscriber list"),
                        PayloadDocumentation.fieldWithPath("result.subscribers[].subscriberId")
                            .type(JsonFieldType.STRING).description("Subscriber Id"),
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
            subscriptionRetrieveHandler.listTargets(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = request,
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
            cursor = Cursor(
                nextCursor = "nextCursor",
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
                            .attributes(RestDocsUtils.remarks("max: 50")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.targets")
                            .type(JsonFieldType.ARRAY).description("subscription target list"),
                        PayloadDocumentation.fieldWithPath("result.targets[].targetId")
                            .type(JsonFieldType.STRING).description("Target Id"),
                        PayloadDocumentation.fieldWithPath("result.targets[].alarmEnabled")
                            .type(JsonFieldType.BOOLEAN).description("alarm Enabled (true/false)"),
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
