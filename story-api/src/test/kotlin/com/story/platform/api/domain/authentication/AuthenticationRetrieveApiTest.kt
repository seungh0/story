package com.story.platform.api.domain.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.workspace.WorkspaceApiResponse
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.workspace.WorkspacePricePlan
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(AuthenticationRetrieveApi::class)
class AuthenticationRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val authenticationRetrieveHandler: AuthenticationRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "인증키 정보를 조회합니다" {
        // given
        val apiKey = "api-key"

        coEvery {
            authenticationRetrieveHandler.getAuthenticationKey(
                apiKey = apiKey,
            )
        } returns AuthenticationApiResponse(
            apiKey = apiKey,
            status = AuthenticationStatus.ENABLED,
            description = "api-key",
            workspace = WorkspaceApiResponse(
                workspaceId = "workspaceId",
                name = "twitter",
                plan = WorkspacePricePlan.FREE,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri("/v1/authentication/api-keys/{apiKey}", apiKey)
            .headers(WebClientUtils.commonHeaders)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "AUTHENTICATION-KEY-GET-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("apiKey").description("Api Key"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.apiKey")
                            .type(JsonFieldType.STRING).description("Api Key"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("Authentication Key Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("Authentication Key Description"),
                        PayloadDocumentation.fieldWithPath("result.workspace.workspaceId")
                            .type(JsonFieldType.STRING).description("Workspace Id"),
                        PayloadDocumentation.fieldWithPath("result.workspace.name")
                            .type(JsonFieldType.STRING).description("Workspace Name"),
                        PayloadDocumentation.fieldWithPath("result.workspace.plan")
                            .type(JsonFieldType.STRING).description("Workspace Plan")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspacePricePlan::class.java))),
                    )
                )
            )
    }

})
