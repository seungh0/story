package com.story.platform.api.domain.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
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
@ApiTest(FeedMappingCreateApi::class)
class FeedMappingCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingCreateHandler: FeedMappingCreateHandler,

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

    "특정 컴포넌트 간에 피드 매핑 설정합니다" {
        // given
        val feedComponentId = "user-timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "user-post"
        val subscriptionComponentId = "user-follow"

        val request = FeedMappingCreateApiRequest(
            description = "유저 포스트가 생성되면 유저를 팔로워 한 구독자들의 타임라인 피드에 발행한다"
        )

        coEvery {
            feedMappingCreateHandler.create(
                workspaceId = any(),
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                request = request,
            )
        } returns Unit

        // when
        val exchange = webTestClient.post()
            .uri(
                "/v1/resources/feeds/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}/to/subscriptions/{subscriptionComponentId}",
                feedComponentId, sourceResourceId.code, sourceComponentId, subscriptionComponentId,
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
                    "feed-mapping.create",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("feedComponentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("sourceResourceId").description("근원 리소스 ID"),
                        RequestDocumentation.parameterWithName("sourceComponentId").description("근원 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriptionComponentId").description("발행할 구독 컴포넌트 ID"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("피드에 대한 설명")
                            .attributes(remarks("최대 300자까지 사용할 수 있습니다"))
                            .optional(),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
