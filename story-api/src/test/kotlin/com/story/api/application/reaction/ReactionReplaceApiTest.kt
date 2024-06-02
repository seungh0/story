package com.story.api.application.reaction

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.RestDocsUtils.remarks
import com.story.api.lib.WebClientUtils
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

@DocsTest
@ApiTest(ReactionReplaceApi::class)
class ReactionReplaceApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val reactionUpsertHandler: ReactionReplaceHandler,
) : FunSpecDocsTest({

    test("대상에 리액션을 대체한다") {
        // given
        val workspaceId = "story"
        val componentId = "post-like"
        val spaceId = "post-id"

        val request = ReactionReplaceRequest(
            emotions = setOf(
                ReactionEmotionUpsertRequest(
                    emotionId = "1",
                ),
                ReactionEmotionUpsertRequest(
                    emotionId = "2",
                )
            )
        )

        coEvery {
            reactionUpsertHandler.replaceReactions(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                request = request,
                userId = any(),
            )
        } returns Unit

        // when
        val exchange = webTestClient.put()
            .uri("/v1/resources/reactions/components/{componentId}/spaces/{spaceId}", componentId, spaceId)
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
                    "reaction.place",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderWithRequestUserIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("리액션 컴포넌트 ID"),
                        parameterWithName("spaceId").description("리액션 공간 ID")
                    ),
                    requestFields(
                        fieldWithPath("emotions").type(JsonFieldType.ARRAY)
                            .description("리액션 이모션 목록")
                            .attributes(remarks("최대 20개까지 등록할 수 있습니다")),
                        fieldWithPath("emotions[].emotionId").type(JsonFieldType.STRING)
                            .description("이모션 ID")
                            .attributes(remarks("최대 100자까지 사용할 수 있습니다")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
