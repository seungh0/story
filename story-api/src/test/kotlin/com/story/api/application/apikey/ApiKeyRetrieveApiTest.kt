package com.story.api.application.apikey

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.application.workspace.WorkspaceResponse
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.WorkspaceEntityFixture
import com.story.core.domain.workspace.WorkspacePricePlan
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(ApiKeyRetrieveApi::class)
class ApiKeyRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val apiKeyRetrieveHandler: ApiKeyRetrieveHandler,
) : StringSpecDocsTest({

    "API-키 정보를 조회합니다" {
        // given
        val apiKey = UUID.randomUUID().toString()

        coEvery {
            apiKeyRetrieveHandler.getApiKey(
                requestApiKey = apiKey,
                filterStatus = any(),
            )
        } returns ApiKeyResponse(
            status = ApiKeyStatus.ENABLED,
            description = "Story Platform에서 사용할 API-Key",
            workspace = WorkspaceResponse.of(
                workspace = WorkspaceEntityFixture.create().toWorkspace(),
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/api-keys/{apiKey}?filterStatus={filterStatus}",
                apiKey,
                ApiKeyStatus.ENABLED
            )
            .headers(WebClientUtils.commonHeaders)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "api-key.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.commonHeadersDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("apiKey").description("API-키"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("filterStatus")
                            .description("필터링 상태 값 (null인 경우 상태 값에 상관 없이 조회)")
                            .optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ApiKeyStatus::class.java))),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("API 키에 대한 상태 값")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ApiKeyStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("API 키에 대한 설명"),
                        PayloadDocumentation.fieldWithPath("result.workspace")
                            .type(JsonFieldType.OBJECT).description("워크스페이스 정보"),
                        PayloadDocumentation.fieldWithPath("result.workspace.workspaceId")
                            .type(JsonFieldType.STRING).description("워크스페이스 ID"),
                        PayloadDocumentation.fieldWithPath("result.workspace.name")
                            .type(JsonFieldType.STRING).description("워크스페이스 이름"),
                        PayloadDocumentation.fieldWithPath("result.workspace.plan")
                            .type(JsonFieldType.STRING).description("워크스페이스 사용 플랜")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspacePricePlan::class.java))),
                        PayloadDocumentation.fieldWithPath("result.workspace.createdAt")
                            .type(JsonFieldType.STRING).description("워크스페이스 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.workspace.updatedAt")
                            .type(JsonFieldType.STRING).description("워크스페이스 수정 일자"),
                    )
                )
            )
    }

})
