package com.story.platform.api.domain.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.event.EventAction
import com.story.platform.core.domain.resource.ResourceId
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(FeedMappingConnectApi::class)
class FeedMappingConnectApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingConnectHandler: FeedMappingConnectHandler,

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

    "특정 컴포넌트를 피드 매핑 설정합니다" {
        // given
        val feedComponentId = "timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "account-post"
        val targetResourceId = ResourceId.SUBSCRIPTIONS
        val targetComponentId = "follow"

        val request = FeedMappingConnectApiRequest(
            eventAction = EventAction.CREATED,
            description = "포스트 등록시 피드 발행"
        )

        coEvery {
            feedMappingConnectHandler.connect(
                workspaceId = any(),
                feedComponentId = feedComponentId,
                targetResourceId = targetResourceId,
                targetComponentId = targetComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                request = request,
            )
        } returns Unit

        // when
        val exchange = webTestClient.post()
            .uri(
                "/v1/feeds/{feedComponentId}/connect/{sourceResourceId}/{sourceComponentId}/to/{targetResourceId}/{targetComponentId}",
                feedComponentId, sourceResourceId.code, sourceComponentId, targetResourceId.code, targetComponentId,
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
                    "FEED-MAPPING-CONNECT-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("feedComponentId").description("Feed Component Id"),
                        RequestDocumentation.parameterWithName("sourceResourceId").description("Source Resource Id"),
                        RequestDocumentation.parameterWithName("sourceComponentId").description("Source Component Id"),
                        RequestDocumentation.parameterWithName("targetResourceId").description("Target Resource Id"),
                        RequestDocumentation.parameterWithName("targetComponentId").description("Target Component Id"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("eventAction").type(JsonFieldType.STRING)
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(EventAction::class.java)))
                            .description("Event Action"),
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("description")
                            .optional(),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
