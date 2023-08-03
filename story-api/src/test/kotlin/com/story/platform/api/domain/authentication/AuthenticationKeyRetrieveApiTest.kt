package com.story.platform.api.domain.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(AuthenticationKeyRetrieveApi::class)
class AuthenticationKeyRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val authenticationKeyRetrieveHandler: AuthenticationKeyRetrieveHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,
) : StringSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
            description = "",
        )
    }

    "인증키 정보를 조회합니다" {
        // given
        val authenticationKey = "authentication-key"
        val description = "API-Key"

        coEvery {
            authenticationKeyRetrieveHandler.getAuthenticationKey(
                authenticationKey = authenticationKey,
            )
        } returns AuthenticationResponse(
            workspaceId = "workspaceId",
            authenticationKey = authenticationKey,
            status = AuthenticationKeyStatus.ENABLED,
            description = description,
        )

        // when
        val exchange = webTestClient.get()
            .uri("/v1/authentication-keys/{authenticationKey}", authenticationKey)
            .headers(WebClientUtils.authenticationHeader)
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
                        RequestDocumentation.parameterWithName("authenticationKey").description("Authentication Key"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.workspaceId")
                            .type(JsonFieldType.STRING).description("Workspace Id"),
                        PayloadDocumentation.fieldWithPath("result.apiKey")
                            .type(JsonFieldType.STRING).description("Authentication Key"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("Authentication Key Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationKeyStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("Authentication Key Description"),
                    )
                )
            )
    }

})
