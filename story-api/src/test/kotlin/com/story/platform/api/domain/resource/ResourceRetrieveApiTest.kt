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
import org.springframework.restdocs.request.RequestDocumentation
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
            workspaceId = "story",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    "사용 가능한 리소스 목록을 조회한다" {
        // given
        val pageSize = 10

        // when
        val exchange = webTestClient.get()
            .uri("/v1/resources?pageSize={pageSize}", pageSize)
            .headers(authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "resource.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지 조회할 수 있습니다")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.resources[]")
                            .type(JsonFieldType.ARRAY).description("리소스 목록"),
                        fieldWithPath("result.resources[].resourceId")
                            .type(JsonFieldType.STRING).description("리소스 ID"),
                        fieldWithPath("result.resources[].description")
                            .type(JsonFieldType.STRING).description("리소스에 대한 설명"),
                    )
                )
            )
    }

})
