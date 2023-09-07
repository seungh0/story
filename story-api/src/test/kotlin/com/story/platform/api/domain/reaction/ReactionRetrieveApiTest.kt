package com.story.platform.api.domain.reaction

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
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
import org.springframework.restdocs.request.RequestDocumentation.relaxedQueryParameters
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
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("대상에 리액션을 취소한다") {
        // given
        val componentId = "post-like"
        val accountId = "accountId"
        val spaceId = "post-id"
        val workspaceId = "twitter"
        val emotionIds = setOf("1", "2")

        coEvery {
            reactionRetrieveHandler.listReactions(
                workspaceId = workspaceId,
                componentId = componentId,
                request = any(),
            )
        } returns ReactionListApiResponse(
            reactions = listOf(
                ReactionResponse(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    spaceId = spaceId,
                    emotions = listOf(
                        ReactionEmotionResponse(
                            emotionId = "1",
                            count = 10500,
                            reactedByMe = true,
                        ),
                        ReactionEmotionResponse(
                            emotionId = "2",
                            count = 3500,
                            reactedByMe = false,
                        )
                    )
                )
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/reactions/components/{componentId}/targets?accountId={accountId}&spaceIds={spaceIds}&emotionIds={emotionIds}",
                componentId,
                accountId,
                setOf(spaceId),
                emotionIds,
            )
            .headers(WebClientUtils.authenticationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "REACTION-LIST-API",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("Reaction Component Id"),
                    ),
                    relaxedQueryParameters(
                        parameterWithName("accountId").description("Reactor Id")
                    ),
                    relaxedQueryParameters(
                        parameterWithName("spaceIds").description("Reaction Space Ids")
                    ),
                    relaxedQueryParameters(
                        parameterWithName("emotionIds").description("Reaction Emotion Ids")
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        fieldWithPath("result.reactions")
                            .type(JsonFieldType.ARRAY).description("Reactions"),
                        fieldWithPath("result.reactions[].workspaceId")
                            .type(JsonFieldType.STRING).description("Reaction Workspace Id"),
                        fieldWithPath("result.reactions[].componentId")
                            .type(JsonFieldType.STRING).description("Reaction Component Id"),
                        fieldWithPath("result.reactions[].spaceId")
                            .type(JsonFieldType.STRING).description("Reaction Space Id"),
                        fieldWithPath("result.reactions[].emotions")
                            .type(JsonFieldType.ARRAY).description("Reaction Emotions"),
                        fieldWithPath("result.reactions[].emotions[].emotionId")
                            .type(JsonFieldType.STRING).description("Reaction Emotion Id"),
                        fieldWithPath("result.reactions[].emotions[].count")
                            .type(JsonFieldType.NUMBER).description("Reaction Emotion selected count"),
                        fieldWithPath("result.reactions[].emotions[].reactedByMe")
                            .type(JsonFieldType.BOOLEAN).description("Whether account reacted to reaction emotion"),
                    )
                )
            )
    }

})
