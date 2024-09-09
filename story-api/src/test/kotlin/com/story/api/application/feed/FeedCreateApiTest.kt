package com.story.api.application.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils.apiKeyHeaderDocumentation
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.RestDocsUtils.remarks
import com.story.api.lib.WebClientUtils
import com.story.core.domain.feed.FeedId
import com.story.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration

@DocsTest
@ApiTest(FeedCreateApi::class)
class FeedCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedCreateHandler: FeedCreateHandler,
) : StringSpecDocsTest({

    "피드함에 피드 목록을 저장합니다" {
        // given
        val componentId = "timeline"
        val subscriberId = "subscriberId"
        val feedId = FeedId(
            itemResourceId = ResourceId.POSTS,
            itemComponentId = "user-posts",
            channelId = "author",
            itemId = "1",
        ).makeKey()

        val request = FeedListCreateRequest(
            feeds = listOf(
                FeedCreateRequest(
                    priority = 1,
                    FeedItemCreateRequest(
                        resourceId = "posts",
                        componentId = "user-posts",
                        channelId = "user-id",
                        itemId = "1"
                    )
                ),
                FeedCreateRequest(
                    priority = 2,
                    FeedItemCreateRequest(
                        resourceId = "subscriptions",
                        componentId = "follow",
                        channelId = "subscriber-id",
                        itemId = "follower-id"
                    )
                )
            ),
            options = FeedItemOptionsCreateRequest(
                retention = Duration.ofDays(1)
            )
        )

        coEvery {
            feedCreateHandler.createFeeds(
                workspaceId = any(),
                componentId = componentId,
                ownerId = subscriberId,
                request = any(),
            )
        } returns Unit

        // when
        val exchange = webTestClient.post()
            .uri(
                "/v1/feed-components/{componentId}/owners/{ownerId}/feeds",
                componentId, subscriberId, feedId,
            )
            .headers(WebClientUtils.apiKeyHeaderWithRequestUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "feed.create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    apiKeyHeaderDocumentation,
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("피드 컴포넌트 ID"),
                        parameterWithName("ownerId").description("피드 소유자 ID"),
                    ),
                    requestFields(
                        fieldWithPath("feeds").type(JsonFieldType.ARRAY)
                            .description("피드 목록")
                            .optional(),
                        fieldWithPath("feeds[].priority").type(JsonFieldType.NUMBER)
                            .description("피드 노출 우선순위"),
                        fieldWithPath("feeds[].item").type(JsonFieldType.OBJECT)
                            .description("피드 아이템"),
                        fieldWithPath("feeds[].item.resourceId").type(JsonFieldType.STRING)
                            .description("피드 아이템 리소스 ID")
                            .attributes(remarks("posts, subscriptions")),
                        fieldWithPath("feeds[].item.componentId").type(JsonFieldType.STRING)
                            .description("피드 아이템 컴포넌트 ID"),
                        fieldWithPath("feeds[].item.channelId").type(JsonFieldType.STRING)
                            .description("피드 아이템 채널 ID"),
                        fieldWithPath("feeds[].item.itemId").type(JsonFieldType.STRING)
                            .description("피드 아이템 ID"),
                        fieldWithPath("options").type(JsonFieldType.OBJECT)
                            .description("피드 저장 옵션"),
                        fieldWithPath("options.retention").type(JsonFieldType.STRING)
                            .description("피드 유지 시간"),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
