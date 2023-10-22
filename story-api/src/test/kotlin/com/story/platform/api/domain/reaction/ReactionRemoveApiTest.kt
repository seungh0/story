package com.story.platform.api.domain.reaction

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.FunSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.WebClientUtils
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
@ApiTest(ReactionRemoveApi::class)
class ReactionRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val reactionRemoveHandler: ReactionRemoveHandler,
) : FunSpecDocsTest({

    test("대상에 리액션을 취소한다") {
        // given
        val workspaceId = "story"
        val componentId = "post-like"
        val accountId = "reactor-id"
        val spaceId = "post-id"

        coEvery {
            reactionRemoveHandler.removeReaction(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
                accountId = any(),
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/resources/reactions/components/{componentId}/spaces/{spaceId}",
                componentId,
                spaceId,
                accountId
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "reaction.remove",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderWithRequestAccountIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("리액션 컴포넌트 ID"),
                        parameterWithName("spaceId").description("리액션 공간 ID")
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
