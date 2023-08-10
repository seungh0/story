package com.story.platform.api.domain.resource

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils.authenticationHeader
import com.story.platform.api.lib.isTrue
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(ResourceRetrieveApi::class, ResourceRetrieveHandler::class)
class ResourceRetrieveApiTest(
    private val webTestClient: WebTestClient,

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

    "사용 가능한 리소스 목록을 조회한다" {
        webTestClient.get()
            .uri("/v1/resources")
            .headers(authenticationHeader)
            .exchange().expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "RESOURCE-LIST-API",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        fieldWithPath("result[]")
                            .type(JsonFieldType.ARRAY).description("result"),
                        fieldWithPath("result[].resourceId")
                            .type(JsonFieldType.STRING).description("Resource Id"),
                        fieldWithPath("result[].description")
                            .type(JsonFieldType.STRING).description("Resource Description"),
                    )
                )
            )
    }

})
