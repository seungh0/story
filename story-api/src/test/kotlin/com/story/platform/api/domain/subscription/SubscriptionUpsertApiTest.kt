package com.story.platform.api.domain.subscription

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
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
@ApiTest(SubscriptionUpsertApi::class)
class SubscriptionUpsertApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val subscriptionUpsertHandler: SubscriptionUpsertHandler,

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
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("Subscription Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId")
                            .description("Subscription Subscriber Id"),
                        RequestDocumentation.parameterWithName("targetId").description("Subscription Target Id"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("alarmEnabled").type(JsonFieldType.BOOLEAN)
                            .description("alarm enabled")
                            .optional()
                            .attributes(RestDocsUtils.remarks("default true"))
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
