package com.story.platform.api.domain.workspace

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.api.lib.isTrue
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.workspace.WorkspaceFixture
import com.story.platform.core.domain.workspace.WorkspacePricePlan
import com.story.platform.core.domain.workspace.WorkspaceResponse
import com.story.platform.core.domain.workspace.WorkspaceStatus
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(WorkspaceRetrieveApi::class)
class WorkspaceRetrieveApiTest(
    private val webTestClient: WebTestClient,

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

    test("워크스페이스를 조회한다") {
        // given
        val workspace = WorkspaceFixture.create(
            workspaceId = "twitter",
            status = WorkspaceStatus.ENABLED,
        )

        coEvery {
            workspaceRetrieveHandler.getWorkspace(
                workspaceId = any(),
            )
        } returns WorkspaceApiResponse.of(workspace = WorkspaceResponse.of(workspace = workspace))

        // when
        val exchange = webTestClient.get()
            .uri("/v1/workspaces/{workspaceId}", workspace.workspaceId)
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "WORKSPACE-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("workspaceId").description("Workspace Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.workspaceId")
                            .type(JsonFieldType.STRING).description("Workspace Id"),
                        PayloadDocumentation.fieldWithPath("result.name")
                            .type(JsonFieldType.STRING).description("Workspace Name"),
                        PayloadDocumentation.fieldWithPath("result.plan")
                            .type(JsonFieldType.STRING).description("Workspace Plan")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspacePricePlan::class.java))),
                    )
                )
            )
    }

})
