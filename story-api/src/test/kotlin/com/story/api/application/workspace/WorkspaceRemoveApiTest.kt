package com.story.api.application.workspace

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.api.lib.isTrue
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(WorkspaceRemoveApi::class)
class WorkspaceRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val workspaceRemoveHandler: WorkspaceRemoveHandler,
) : FunSpecDocsTest({

    test("워크스페이스를 삭제합니다") {
        // given
        val workspaceId = "story"

        coEvery {
            workspaceRemoveHandler.removeWorkspace(
                workspaceId = workspaceId
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri("/v1/workspaces/{workspaceId}", workspaceId)
            .headers(WebClientUtils.apiKeyHeader)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "workspace.remove",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("workspaceId").description("워크스페이스 ID"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부")
                    )
                )
            )
    }

})
