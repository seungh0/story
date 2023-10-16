package com.story.platform.api.domain.reaction

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.authenticationHeaderWithRequestAccountIdDocumentation
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.emotion.EmotionResponse
import com.story.platform.core.domain.reaction.ReactionEmotionResponse
import com.story.platform.core.domain.reaction.ReactionResponse
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
@ApiTest(ReactionRetrieveApi::class)
class ReactionRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val reactionRetrieveHandler: ReactionRetrieveHandler,

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

    test("대상에 등록된 리액션 목록을 조회한다") {
        // given
        val workspaceId = "story"
        val componentId = "post-like"
        val spaceId = "post-id"
        val includeUnselectedEmotions = true

        coEvery {
            reactionRetrieveHandler.getReaction(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                requestAccountId = any(),
                request = any(),
            )
        } returns ReactionApiResponse.of(
            reaction = ReactionResponse(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                emotions = listOf(
                    ReactionEmotionResponse(
                        emotionId = "emotion-1",
                        count = 10500,
                        reactedByMe = true,
                    ),
                    ReactionEmotionResponse(
                        emotionId = "emotion-2",
                        count = 3500,
                        reactedByMe = false,
                    )
                )
            ),
            emotions = mapOf(
                "emotion-1" to EmotionResponse(
                    emotionId = "emotion-1",
                    image = "\uD83D\uDE21",
                    priority = 1,
                ),
                "emotion-2" to EmotionResponse(
                    emotionId = "emotion-2",
                    image = "\uD83E\uDEE4",
                    priority = 2,
                )
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/reactions/components/{componentId}/spaces/{spaceId}?includeUnselectedEmotions={includeUnselectedEmotions}",
                componentId,
                spaceId,
                includeUnselectedEmotions,
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "reaction.get",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(
                        "- Component에 등록되지 않은 Emotion은 결과에 포함되지 않습니다."
                    ),
                    authenticationHeaderWithRequestAccountIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("리액션 컴포넌트 ID"),
                        parameterWithName("spaceId").description("리액션 공간 ID")
                    ),
                    queryParameters(
                        parameterWithName("includeUnselectedEmotions").description("미선택된 이모션들도 응답에 포함할지 여부")
                            .attributes(RestDocsUtils.remarks("default false"))
                            .optional(),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.spaceId")
                            .type(JsonFieldType.STRING).description("리액션 공간 Id"),
                        fieldWithPath("result.emotions")
                            .type(JsonFieldType.ARRAY).description("리액션에 등록된 이모션 목록"),
                        fieldWithPath("result.emotions[].emotionId")
                            .type(JsonFieldType.STRING).description("이모션 ID"),
                        fieldWithPath("result.emotions[].priority")
                            .type(JsonFieldType.NUMBER).description("이모션 우선순위")
                            .attributes(RestDocsUtils.remarks("우선순위가 낮은 것부터 정렬되서 반환됩니다")),
                        fieldWithPath("result.emotions[].image")
                            .type(JsonFieldType.STRING).description("이모션 이미지"),
                        fieldWithPath("result.emotions[].count")
                            .type(JsonFieldType.NUMBER).description("이모션 선택 횟수"),
                        fieldWithPath("result.emotions[].reactedByMe")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 이모션 선택 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-Account-Id 헤더를 기준으로 합니다")),
                    )
                )
            )
    }

    test("대상 목록에 등록된 리액션 목록을 조회한다") {
        // given
        val workspaceId = "story"
        val componentId = "post-like"
        val spaceIds = "post-spaceId-1,post-spaceId-2"
        val includeUnselectedEmotions = true

        coEvery {
            reactionRetrieveHandler.listReactions(
                workspaceId = workspaceId,
                componentId = componentId,
                request = any(),
                requestAccountId = any(),
            )
        } returns ReactionListApiResponse(
            reactions = listOf(
                ReactionApiResponse.of(
                    reaction = ReactionResponse(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        spaceId = "space-1",
                        emotions = listOf(
                            ReactionEmotionResponse(
                                emotionId = "emotion-1",
                                count = 10500,
                                reactedByMe = true,
                            ),
                            ReactionEmotionResponse(
                                emotionId = "emotion-2",
                                count = 3500,
                                reactedByMe = false,
                            )
                        )
                    ),
                    emotions = mapOf(
                        "emotion-1" to EmotionResponse(
                            emotionId = "emotion-1",
                            image = "\uD83D\uDE21",
                            priority = 1,
                        ),
                        "emotion-2" to EmotionResponse(
                            emotionId = "emotion-2",
                            image = "\uD83E\uDEE4",
                            priority = 2,
                        )
                    )
                ),
                ReactionApiResponse.of(
                    reaction = ReactionResponse(
                        workspaceId = workspaceId,
                        componentId = componentId,
                        spaceId = "space-2",
                        emotions = listOf(
                            ReactionEmotionResponse(
                                emotionId = "emotion-1",
                                count = 3500,
                                reactedByMe = false,
                            ),
                            ReactionEmotionResponse(
                                emotionId = "emotion-2",
                                count = 1000,
                                reactedByMe = true,
                            )
                        )
                    ),
                    emotions = mapOf(
                        "emotion-1" to EmotionResponse(
                            emotionId = "emotion-1",
                            image = "\uD83D\uDE21",
                            priority = 1,
                        ),
                        "emotion-2" to EmotionResponse(
                            emotionId = "emotion-2",
                            image = "\uD83E\uDEE4",
                            priority = 2,
                        )
                    )
                )
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/reactions/components/{componentId}/spaces?includeUnselectedEmotions={includeUnselectedEmotions}&spaceIds={spaceIds}",
                componentId,
                includeUnselectedEmotions,
                spaceIds,
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "reaction.list",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(
                        "- Component에 등록되지 않은 Emotion은 결과에 포함되지 않습니다."
                    ),
                    authenticationHeaderWithRequestAccountIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("리액션 컴포넌트 ID"),
                    ),
                    queryParameters(
                        parameterWithName("includeUnselectedEmotions").description("미선택된 이모션들도 응답에 포함할지 여부")
                            .attributes(RestDocsUtils.remarks("default false"))
                            .optional(),
                        parameterWithName("spaceIds").description("리액션 공간 ID 목록"),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.reactions")
                            .type(JsonFieldType.ARRAY).description("리액션 목록"),
                        fieldWithPath("result.reactions[].spaceId")
                            .type(JsonFieldType.STRING).description("리액션 공간 Id"),
                        fieldWithPath("result.reactions[].emotions")
                            .type(JsonFieldType.ARRAY).description("리액션에 등록된 이모션 목록"),
                        fieldWithPath("result.reactions[].emotions[].emotionId")
                            .type(JsonFieldType.STRING).description("이모션 Id"),
                        fieldWithPath("result.reactions[].emotions[].priority")
                            .type(JsonFieldType.NUMBER).description("이모션 우선순위")
                            .attributes(RestDocsUtils.remarks("우선순위가 낮은 것부터 정렬되서 반환됩니다")),
                        fieldWithPath("result.reactions[].emotions[].image")
                            .type(JsonFieldType.STRING).description("이모션 이미지"),
                        fieldWithPath("result.reactions[].emotions[].count")
                            .type(JsonFieldType.NUMBER).description("이모션 선택 횟수"),
                        fieldWithPath("result.reactions[].emotions[].reactedByMe")
                            .type(JsonFieldType.BOOLEAN).description("요청자의 이모션 선택 여부")
                            .attributes(RestDocsUtils.remarks("요청자는 X-Request-Account-Id 헤더를 기준으로 합니다")),
                    )
                )
            )
    }

})
