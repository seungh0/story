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
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(AuthenticationModifyApi::class)
class AuthenticationModifyApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val authenticationModifyHandler: AuthenticationModifyHandler,

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

    "인증 키에 대한 정보를 변경합니다" {
        // given
        val authenticationKey = UUID.randomUUID().toString()
        val description = "story platform api key"
        val status = AuthenticationStatus.ENABLED

        val request = AuthenticationModifyApiRequest(
            description = description,
            status = status,
        )

        coEvery {
            authenticationModifyHandler.patchAuthentication(
                workspaceId = any(),
                authenticationKey = authenticationKey,
                description = description,
                status = status,
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri("/v1/authentication/{authenticationKey}", authenticationKey)
            .headers(WebClientUtils.authenticationHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "authentication.modify",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("authenticationKey").description("Authentication Key"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("Authentication Description")
                            .attributes(RestDocsUtils.remarks("must be within 300 characters"))
                            .optional(),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("Authentication Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java)))
                            .optional(),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
