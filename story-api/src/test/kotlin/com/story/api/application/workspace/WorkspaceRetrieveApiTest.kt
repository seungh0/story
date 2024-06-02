package com.story.api.application.workspace

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.application.apikey.ApiKeyHandler
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.api.lib.isTrue
import com.story.core.domain.apikey.ApiKey
import com.story.core.domain.apikey.ApiKeyStatus
import com.story.core.domain.workspace.Workspace
import com.story.core.domain.workspace.WorkspaceFixture
import com.story.core.domain.workspace.WorkspacePricePlan
import com.story.core.domain.workspace.WorkspaceStatus
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
    private val apiKeyHandler: ApiKeyHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { apiKeyHandler.handleApiKey(any()) } returns ApiKey(
            workspaceId = "story",
            status = ApiKeyStatus.ENABLED,
            description = "Story Platform",
            apiKey = "api-key",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("워크스페이스를 조회한다") {
        // given
        val workspace = WorkspaceFixture.create(
            workspaceId = "story",
            name = "Story Platform",
            status = WorkspaceStatus.ENABLED,
        )

        coEvery {
            workspaceRetrieveHandler.getWorkspace(
                workspaceId = any(),
                filterStatus = any(),
            )
        } returns WorkspaceApiResponse.of(workspace = Workspace.of(workspace = workspace))

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/workspaces/{workspaceId}?filterStatus={filterStatus}",
                workspace.workspaceId,
                WorkspaceStatus.ENABLED
            )
            .headers(WebClientUtils.apiKeyHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "workspace.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("workspaceId").description("워크스페이스 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("filterStatus")
                            .description("필터링 상태 값 (null인 경우 상태 값에 상관 없이 조회)")
                            .optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspaceStatus::class.java))),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.workspaceId")
                            .type(JsonFieldType.STRING).description("워크스페이스 ID"),
                        PayloadDocumentation.fieldWithPath("result.name")
                            .type(JsonFieldType.STRING).description("워크스페이스의 이름"),
                        PayloadDocumentation.fieldWithPath("result.plan")
                            .type(JsonFieldType.STRING).description("워크스페이스의 사용 플랜")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(WorkspacePricePlan::class.java))),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("워크스페이스 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("워크스페이스 최근 수정 일자"),
                    )
                )
            )
    }

})
