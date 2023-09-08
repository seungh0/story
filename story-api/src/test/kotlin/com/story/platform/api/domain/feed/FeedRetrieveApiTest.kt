package com.story.platform.api.domain.feed

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.domain.subscription.SubscriptionEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@DocsTest
@ApiTest(FeedRetrieveApi::class)
class FeedRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val feedRetrieveHandler: FeedRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "피드를 조회합니다 (Post)" {
        // given
        val componentId = "timeline"
        val subscriberId = "subscriberId"

        coEvery {
            feedRetrieveHandler.listFeeds(
                workspaceId = any(),
                feedComponentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = any(),
            )
        } returns CursorResult.of(
            data = listOf(
                FeedApiResponse(
                    feedId = "30000",
                    resourceId = ResourceId.POSTS.code,
                    componentId = "account-post",
                    payload = PostEvent(
                        workspaceId = "twitter",
                        resourceId = ResourceId.POSTS,
                        componentId = "account-post",
                        spaceId = "accountId",
                        postId = 1000L,
                        accountId = "account-id",
                        title = "Post Title",
                        content = "Post Content",
                        extra = mapOf("key" to "value"),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )
                )
            ),
            cursor = Cursor(
                hasNext = true,
                nextCursor = "nextCursor",
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/feeds/components/{componentId}/subscriber/{subscriberId}?cursor=cursor&direction=NEXT&pageSize=30",
                componentId, subscriberId
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "FEED-LIST-API-POST",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("FEED Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId").description("FEED Subscriber Id"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("Cursor").optional()
                            .attributes(RestDocsUtils.remarks("first cursor is null")),
                        RequestDocumentation.parameterWithName("direction").description("Direction").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(CursorDirection::class.java) + "\n(default: NEXT)")),
                        RequestDocumentation.parameterWithName("pageSize").description("Page Size")
                            .attributes(RestDocsUtils.remarks("max: 30")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.data")
                            .type(JsonFieldType.ARRAY).description("feed list"),
                        PayloadDocumentation.fieldWithPath("result.data[].feedId")
                            .type(JsonFieldType.STRING).description("Feed Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].resourceId")
                            .type(JsonFieldType.STRING).description("Resource Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].componentId")
                            .type(JsonFieldType.STRING).description("Component Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload")
                            .type(JsonFieldType.OBJECT).description("Payload"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.workspaceId")
                            .type(JsonFieldType.STRING).description("Post WorkspaceId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.resourceId")
                            .type(JsonFieldType.STRING).description("Post ResourceId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.componentId")
                            .type(JsonFieldType.STRING).description("Post ComponentId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.spaceId")
                            .type(JsonFieldType.STRING).description("Post Space Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.postId")
                            .type(JsonFieldType.NUMBER).description("Post Id"), // TODO: Number -> String 변환 필요
                        PayloadDocumentation.fieldWithPath("result.data[].payload.accountId")
                            .type(JsonFieldType.STRING).description("Post Owner Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.title")
                            .type(JsonFieldType.STRING).description("Post Title"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.content")
                            .type(JsonFieldType.STRING).description("Post Content"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.extra")
                            .type(JsonFieldType.OBJECT).description("Post extra value"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.extra.key")
                            .type(JsonFieldType.STRING).description("Post extra key").optional(),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.extra.value")
                            .type(JsonFieldType.STRING).description("Post extra value").optional(),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.createdAt")
                            .type(JsonFieldType.STRING).description("Post Created At"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.updatedAt")
                            .type(JsonFieldType.STRING).description("Post Updated At"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("nextCursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("hasNext"),
                    )
                )
            )
    }

    "피드를 조회합니다 (Subscription)" {
        // given
        val componentId = "timeline"
        val subscriberId = "subscriberId"

        coEvery {
            feedRetrieveHandler.listFeeds(
                workspaceId = any(),
                feedComponentId = componentId,
                subscriberId = subscriberId,
                cursorRequest = any(),
            )
        } returns CursorResult.of(
            data = listOf(
                FeedApiResponse(
                    feedId = "30000",
                    resourceId = ResourceId.SUBSCRIPTIONS.code,
                    componentId = "follow",
                    payload = SubscriptionEvent(
                        workspaceId = "twitter",
                        resourceId = ResourceId.SUBSCRIPTIONS,
                        componentId = "follow",
                        subscriberId = "subscriberId",
                        targetId = "targetId",
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                    )
                )
            ),
            cursor = Cursor(
                hasNext = true,
                nextCursor = "nextCursor",
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/feeds/components/{componentId}/subscriber/{subscriberId}?cursor=cursor&direction=NEXT&pageSize=30",
                componentId, subscriberId
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "FEED-LIST-API-SUBSCRIPTION",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("componentId").description("FEED Component Id"),
                        RequestDocumentation.parameterWithName("subscriberId").description("FEED Subscriber Id"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("Cursor").optional()
                            .attributes(RestDocsUtils.remarks("first cursor is null")),
                        RequestDocumentation.parameterWithName("direction").description("Direction").optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(CursorDirection::class.java) + "\n(default: NEXT)")),
                        RequestDocumentation.parameterWithName("pageSize").description("Page Size")
                            .attributes(RestDocsUtils.remarks("max: 30")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.data")
                            .type(JsonFieldType.ARRAY).description("feed list"),
                        PayloadDocumentation.fieldWithPath("result.data[].feedId")
                            .type(JsonFieldType.STRING).description("Feed Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].resourceId")
                            .type(JsonFieldType.STRING).description("Resource Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].componentId")
                            .type(JsonFieldType.STRING).description("Component Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload")
                            .type(JsonFieldType.OBJECT).description("Payload"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.workspaceId")
                            .type(JsonFieldType.STRING).description("Subscription WorkspaceId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.resourceId")
                            .type(JsonFieldType.STRING).description("Subscription ResourceId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.componentId")
                            .type(JsonFieldType.STRING).description("Subscription ComponentId"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.subscriberId")
                            .type(JsonFieldType.STRING).description("Subscriber Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.targetId")
                            .type(JsonFieldType.STRING).description("Subscription Target Id"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.createdAt")
                            .type(JsonFieldType.STRING).description("Post Created At"),
                        PayloadDocumentation.fieldWithPath("result.data[].payload.updatedAt")
                            .type(JsonFieldType.STRING).description("Post Updated At"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("nextCursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("hasNext"),
                    )
                )
            )
    }

})
