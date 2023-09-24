package com.story.platform.api.domain.nonce

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
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.nonce.NonceManager
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(NonceCreateApi::class)
class NonceCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val nonceManager: NonceManager,

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
        coEvery { nonceManager.generate(any()) } returns UUID.randomUUID().toString()
    }

    test("새로운 Nonce를 할당 받습니다") {
        // given
        coEvery {
            nonceManager.generate(any())
        } returns UUID.randomUUID().toString()

        val request = NonceCreateApiRequest(
            expirationSeconds = 3_600,
        )

        // when
        val exchange = webTestClient.post()
            .uri("/v1/nonce")
            .headers(WebClientUtils.authenticationHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "NONCE-CREATE-API",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    requestFields(
                        fieldWithPath("expirationSeconds").type(JsonFieldType.NUMBER)
                            .description("Expiration Duration (seconds)")
                            .optional()
                            .attributes(RestDocsUtils.remarks("Should be greater than 0 and less than or equal to 86,400(1D). (default: 3600, 1H)")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        fieldWithPath("result.nonce")
                            .type(JsonFieldType.STRING).description("Nonce"),
                    )
                )
            )
    }

})
