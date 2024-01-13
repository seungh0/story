package com.story.api.application.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.remarks
import com.story.api.lib.WebClientUtils
import com.story.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

@DocsTest
@ApiTest(FeedMappingCreateApi::class)
class FeedMappingCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedMappingCreateHandler: FeedMappingCreateHandler,
) : StringSpecDocsTest({

    "특정 컴포넌트 간에 피드 매핑 설정합니다" {
        // given
        val feedComponentId = "user-timeline"
        val sourceResourceId = ResourceId.POSTS
        val sourceComponentId = "user-post"
        val subscriptionComponentId = "user-follow"

        val request = FeedMappingCreateApiRequest(
            description = "유저 포스트가 생성되면 유저를 팔로워 한 구독자들의 타임라인 피드에 발행한다",
            retention = Duration.ofDays(30),
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
                        PayloadDocumentation.fieldWithPath("retention").type(JsonFieldType.STRING)
                            .description("피드 데이터 유지 시간")
                            .attributes(remarks("최소 1분 부터 최대 100일까지 설정할 수 있습니다")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
