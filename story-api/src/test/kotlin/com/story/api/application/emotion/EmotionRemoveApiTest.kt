package com.story.api.application.emotion

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.WebClientUtils
import com.story.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(EmotionRemoveApi::class)
class EmotionRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val emotionRemoveHandler: EmotionRemoveHandler,
) : FunSpecDocsTest({

    test("이모션을 삭제합니다") {
        // given
        val resourceId = ResourceId.REACTIONS
        val componentId = "post-sticker"
        val emotionId = "emotion-id"

        coEvery {
            emotionRemoveHandler.removeEmotion(
                workspaceId = "story",
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/resources/{resourceId}/components/{componentId}/emotions/{emotionId}",
                resourceId.code,
                componentId,
                emotionId,
            )
            .headers(WebClientUtils.apiKeyHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "emotion.remove",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    pathParameters(
                        parameterWithName("resourceId").description("이모션을 사용할 리소스 ID"),
                        parameterWithName("componentId").description("이모션을 사용할 컴포넌트 ID"),
                        parameterWithName("emotionId").description("이모션 ID"),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
