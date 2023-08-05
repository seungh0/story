package com.story.platform.api.domain.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationKeyResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
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
@ApiTest(FeedMappingDisconnectApi::class)
class FeedMappingDisconnectApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingDisconnectHandler: FeedMappingDisconnectHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationKeyResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "특정 컴포넌트 간의 피드 매핑을 해제합니다" {
        // given
        val feedComponentId = "timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "account-post"
        val targetResourceId = ResourceId.SUBSCRIPTIONS
        val targetComponentId = "follow"

        val request = FeedMappingDisconnectApiRequest(
            eventAction = EventAction.CREATED,
        )

        coEvery {
            feedMappingDisconnectHandler.disconnect(
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
        val exchange = webTestClient.delete()
            .uri(
                "/v1/feeds/{feedComponentId}/connect/{sourceResourceId}/{sourceComponentId}/to/{targetResourceId}/{targetComponentId}?eventAction=${request.eventAction}",
                feedComponentId, sourceResourceId.code, sourceComponentId, targetResourceId.code, targetComponentId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "FEED-MAPPING-DISCONNECT-API",
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
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("eventAction").description("EventAction")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(EventAction::class.java))),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
