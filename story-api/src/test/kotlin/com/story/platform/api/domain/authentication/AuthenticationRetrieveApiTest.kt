package com.story.platform.api.domain.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

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
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "인증키 정보를 조회합니다" {
        // given
        val authenticationKey = UUID.randomUUID().toString()

        coEvery {
            authenticationRetrieveHandler.getAuthentication(
                authenticationKey = authenticationKey,
            )
        } returns AuthenticationApiResponse(
            authenticationKey = authenticationKey,
            status = AuthenticationStatus.ENABLED,
            description = "story platform api key"
        )

        // when
        val exchange = webTestClient.get()
            .uri("/v1/authentication/{authenticationKey}", authenticationKey)
            .headers(WebClientUtils.commonHeaders)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "authentication.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("authenticationKey").description("Authentication Key"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.authenticationKey")
                            .type(JsonFieldType.STRING).description("Authentication Key"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("Authentication Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("Authentication Description"),
                    )
                )
            )
    }

})
