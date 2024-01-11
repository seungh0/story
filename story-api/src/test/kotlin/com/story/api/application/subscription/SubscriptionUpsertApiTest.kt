package com.story.api.application.subscription

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(SubscriptionUpsertApi::class)
class SubscriptionUpsertApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val subscriptionUpsertHandler: SubscriptionUpsertHandler,
) : StringSpecDocsTest({

    "대상을 구독합니다" {
        // given
        val componentId = "follow"
        val subscriberId = "subscriber-id"
        val targetId = "subscription-target-id"

        val request = SubscriptionUpsertApiRequest(
            alarmEnabled = true,
        )

        coEvery {
            subscriptionUpsertHandler.upsertSubscription(
                workspaceId = any(),
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = request.alarmEnabled,
            )
        } returns Unit

        // when
        val exchange = webTestClient.put()
            .uri(
                "/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}",
                componentId, subscriberId, targetId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "subscription.upsert",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("구독 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("구독자 ID"),
                        RequestDocumentation.parameterWithName("targetId").description("구독 대상 ID"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("alarmEnabled").type(JsonFieldType.BOOLEAN)
                            .type(JsonFieldType.BOOLEAN).description("구독 대상에 대한 알림 설정 여부")
                            .optional()
                            .attributes(RestDocsUtils.remarks("기본 값: true")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
