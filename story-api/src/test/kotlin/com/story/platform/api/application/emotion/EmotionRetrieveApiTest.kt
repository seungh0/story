package com.story.platform.api.application.emotion

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.FunSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.resource.ResourceId
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
@ApiTest(EmotionRetrieveApi::class)
class EmotionRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val emotionRetrieveHandler: EmotionRetrieveHandler,
) : FunSpecDocsTest({

    test("이모션 목록을 조회한다") {
        // given
        val resourceId = ResourceId.REACTIONS
        val componentId = "post-sticker"

        coEvery {
            emotionRetrieveHandler.listEmotions(
                workspaceId = "story",
                resourceId = resourceId,
                componentId = componentId,
            )
        } returns EmotionListApiResponse(
            emotions = listOf(
                EmotionApiResponse(
                    emotionId = "smile",
                    image = "\uD83D\uDC08",
                    priority = 1,
                ),
                EmotionApiResponse(
                    emotionId = "sad",
                    image = "\uD83D\uDE2D",
                    priority = 2,
                )
            ),
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/{resourceId}/components/{componentId}/emotions",
                resourceId.code,
                componentId,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "emotion.list",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    pathParameters(
                        parameterWithName("resourceId").description("이모션을 사용할 리소스 ID"),
                        parameterWithName("componentId").description("이모션을 사용할 컴포넌트 ID"),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.emotions[]")
                            .type(JsonFieldType.ARRAY).description("이모션 목록"),
                        fieldWithPath("result.emotions[].emotionId")
                            .type(JsonFieldType.STRING).description("이모션 Id"),
                        fieldWithPath("result.emotions[].priority")
                            .type(JsonFieldType.NUMBER).description("이모션 우선순위")
                            .attributes(RestDocsUtils.remarks("우선순위가 낮은 것부터 정렬되서 반환됩니다 (기본값: 0)"))
                            .optional(),
                        fieldWithPath("result.emotions[].image")
                            .type(JsonFieldType.STRING).description("이모션 이미지"),
                    )
                )
            )
    }

})