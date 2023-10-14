package com.story.platform.api.domain.emotion

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.resource.ResourceId
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(EmotionRetrieveApi::class)
class EmotionRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val emotionRetrieveHandler: EmotionRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("이모션 목록을 조회한다") {
        // given
        val resourceId = ResourceId.REACTIONS
        val componentId = "post-sticker"
        val cursor = "cursor"
        val pageSize = 10

        coEvery {
            emotionRetrieveHandler.listEmotions(
                workspaceId = "story",
                resourceId = resourceId,
                componentId = componentId,
                cursorRequest = any(),
            )
        } returns EmotionListApiResponse(
            emotions = listOf(
                EmotionApiResponse(
                    emotionId = "smile",
                    image = "\uD83D\uDC08"
                ),
                EmotionApiResponse(
                    emotionId = "sad",
                    image = "\uD83D\uDE2D",
                )
            ),
            cursor = Cursor.of(
                cursor = "next-cursor"
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/{resourceId}/components/{componentId}/emotions?cursor={cursor}&pageSize={pageSize}",
                resourceId.code,
                componentId,
                cursor,
                pageSize,
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
                        parameterWithName("resourceId").description("Resource Id"),
                        parameterWithName("componentId").description("Component Id"),
                    ),
                    queryParameters(
                        parameterWithName("cursor").description("Cursor").optional()
                            .attributes(RestDocsUtils.remarks("first cursor is null")),
                        parameterWithName("pageSize").description("Page Size")
                            .attributes(RestDocsUtils.remarks("max: 50")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        fieldWithPath("result.emotions[]")
                            .type(JsonFieldType.ARRAY).description("resources"),
                        fieldWithPath("result.emotions[].emotionId")
                            .type(JsonFieldType.STRING).description("Emotion Id"),
                        fieldWithPath("result.emotions[].image")
                            .type(JsonFieldType.STRING).description("Emotion Image"),
                        fieldWithPath("result.cursor")
                            .type(JsonFieldType.OBJECT).description("Page Cursor"),
                        fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("Next Page Cursor").optional(),
                        fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("Has More Page (next direction)"),
                    )
                )
            )
    }

})
