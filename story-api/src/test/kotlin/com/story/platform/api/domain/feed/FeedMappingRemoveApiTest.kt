package com.story.platform.api.domain.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.StringSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(FeedMappingRemoveApi::class)
class FeedMappingRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingRemoveHandler: FeedMappingRemoveHandler,
) : StringSpecDocsTest({

    "특정 컴포넌트 간의 피드 매핑을 해제합니다" {
        // given
        val feedComponentId = "user-timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "user-post"
        val subscriptionComponentId = "user-follow"

        coEvery {
            feedMappingRemoveHandler.remove(
                workspaceId = any(),
                feedComponentId = feedComponentId,
                subscriptionComponentId = subscriptionComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/resources/feeds/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}/to/subscriptions/{subscriptionComponentId}",
                feedComponentId, sourceResourceId.code, sourceComponentId, subscriptionComponentId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "feed-mapping.remove",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("feedComponentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("sourceResourceId").description("근원 리소스 ID"),
                        RequestDocumentation.parameterWithName("sourceComponentId").description("근원 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriptionComponentId").description("발행할 구독 컴포넌트 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
