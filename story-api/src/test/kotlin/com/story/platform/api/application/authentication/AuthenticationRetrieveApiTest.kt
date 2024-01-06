package com.story.platform.api.application.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.StringSpecDocsTest
import com.story.platform.api.application.workspace.WorkspaceApiResponse
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.workspace.WorkspaceFixture
import com.story.platform.core.domain.workspace.WorkspacePricePlan
import com.story.platform.core.domain.workspace.WorkspaceResponse
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
) : StringSpecDocsTest({

    "인증키 정보를 조회합니다" {
        // given
        val authenticationKey = UUID.randomUUID().toString()

        coEvery {
            authenticationRetrieveHandler.getAuthentication(
                authenticationKey = authenticationKey,
                filterStatus = any(),
            )
        } returns AuthenticationApiResponse(
            authenticationKey = authenticationKey,
            status = AuthenticationStatus.ENABLED,
            description = "Story Platform에서 사용할 인증 키",
            workspace = WorkspaceApiResponse.of(
                workspace = WorkspaceResponse.of(workspace = WorkspaceFixture.create())
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/authentication/{authenticationKey}?filterStatus={filterStatus}",
                authenticationKey,
                AuthenticationStatus.ENABLED
            )
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
                    RestDocsUtils.commonHeadersDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("authenticationKey").description("인증 키"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("filterStatus")
                            .description("필터링 상태 값 (null인 경우 상태 값에 상관 없이 조회)")
                            .optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java))),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.authenticationKey")
                            .type(JsonFieldType.STRING).description("인증 키"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("인증 키에 대한 상태 값")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("인증 정보에 대한 설명"),
                        PayloadDocumentation.fieldWithPath("result.workspace")
                            .type(JsonFieldType.OBJECT).description("워크스페이스 정보"),
                        PayloadDocumentation.fieldWithPath("result.workspace.workspaceId")
                            .type(JsonFieldType.STRING).description("워크스페이스 ID"),
                        PayloadDocumentation.fieldWithPath("result.workspace.name")
                            .type(JsonFieldType.STRING).description("워크스페이스 이름"),
                        PayloadDocumentation.fieldWithPath("result.workspace.plan")
                            .type(JsonFieldType.STRING).description("워크스페이스 사용 플랜")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspacePricePlan::class.java))),
                    )
                )
            )
    }

})
