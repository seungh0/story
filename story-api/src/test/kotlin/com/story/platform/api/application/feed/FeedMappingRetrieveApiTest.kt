package com.story.platform.api.application.feed

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
@ApiTest(FeedMappingRetrieveApi::class)
class FeedMappingRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingRetrieveHandler: FeedMappingRetrieveHandler,
) : StringSpecDocsTest({

    "설정된 피드 매핑 목록을 조회합니다" {
        // given
        val feedComponentId = "user-timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "user-post"

        coEvery {
            feedMappingRetrieveHandler.listConnectedFeedMappings(
                workspaceId = any(),
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
            )
        } returns FeedMappingListApiResponse(
            feedMappings = listOf(
                FeedMappingApiResponse(
                    resourceId = ResourceId.SUBSCRIPTIONS.code,
                    componentId = "follow"
                )
            ),
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/feeds/{feedComponentId}/mappings/{sourceResourceId}/{sourceComponentId}",
                feedComponentId, sourceResourceId.code, sourceComponentId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "feed-mapping.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("feedComponentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("sourceResourceId").description("근원 리소스 ID"),
                        RequestDocumentation.parameterWithName("sourceComponentId").description("근원 컴포넌트 ID"),
                    ),
                    RequestDocumentation.queryParameters(),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.feedMappings")
                            .type(JsonFieldType.ARRAY).description("피드 매핑 목록"),
                        PayloadDocumentation.fieldWithPath("result.feedMappings[].resourceId")
                            .type(JsonFieldType.STRING).description("피드 매핑 근원 리소스 ID"),
                        PayloadDocumentation.fieldWithPath("result.feedMappings[].componentId")
                            .type(JsonFieldType.STRING).description("피드 매핑 근원 컴포넌트 ID"),
                    )
                )
            )
    }

})