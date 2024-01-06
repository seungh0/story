package com.story.platform.api.application.emotion

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.FunSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.authenticationHeaderDocumentation
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.resource.ResourceId
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
@ApiTest(EmotionModifyApi::class)
class EmotionModifyApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val emotionModifyHandler: EmotionModifyHandler,
) : FunSpecDocsTest({

    test("새로운 이모션을 등록한다") {
        // given
        val resourceId = ResourceId.REACTIONS
        val componentId = "post-sticker"
        val emotionId = "emotion-id"

        val request = EmotionModifyApiRequest(
            image = "\uD83D\uDE49",
            priority = 1,
        )

        coEvery {
            emotionModifyHandler.modifyEmotion(
                workspaceId = "story",
                resourceId = resourceId,
                componentId = componentId,
                emotionId = emotionId,
                request = request,
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri(
                "/v1/resources/{resourceId}/components/{componentId}/emotions/{emotionId}",
                resourceId.code,
                componentId,
                emotionId,
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
                document(
                    "emotion.modify",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    authenticationHeaderDocumentation,
                    pathParameters(
                        parameterWithName("resourceId").description("이모션을 사용할 리소스 ID"),
                        parameterWithName("componentId").description("이모션을 사용할 컴포넌트 ID"),
                        parameterWithName("emotionId").description("이모션 ID"),
                    ),
                    requestFields(
                        fieldWithPath("image").type(JsonFieldType.STRING)
                            .description("이모션 이미지")
                            .optional(),
                        fieldWithPath("priority")
                            .type(JsonFieldType.NUMBER).description("이모션 우선순위")
                            .attributes(RestDocsUtils.remarks("우선순위가 낮은 것부터 정렬되서 반환됩니다 (기본값: 0)"))
                            .optional(),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
