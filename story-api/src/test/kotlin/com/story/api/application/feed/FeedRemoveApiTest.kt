package com.story.api.application.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(FeedRemoveApi::class)
class FeedRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedRemoveHandler: FeedRemoveHandler,
) : StringSpecDocsTest({

    "특정 컴포넌트 간의 피드 매핑을 해제합니다" {
        // given
        val componentId = "timeline"
        val subscriberId = "subscriberId"
        val feedId = 213123L

        coEvery {
            feedRemoveHandler.remove(
                workspaceId = any(),
                componentId = componentId,
                subscriberId = subscriberId,
                feedId = feedId,

            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/resources/feeds/components/{componentId}/subscribers/{subscriberId}/feeds/{feedId}",
                componentId, subscriberId, feedId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "feed.remove",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("피드 컴포넌트 ID"),
                        RequestDocumentation.parameterWithName("subscriberId").description("피드 구독자 ID"),
                        RequestDocumentation.parameterWithName("feedId").description("피드 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
