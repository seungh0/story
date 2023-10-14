package com.story.platform.api.domain.component

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.resource.ResourceId
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@DocsTest
@ApiTest(ComponentRetrieveApi::class)
class ComponentRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val componentRetrieveHandler: ComponentRetrieveHandler,

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

    "컴포넌트를 조회합니다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "story user following system"
        val status = ComponentStatus.ENABLED

        val component = ComponentApiResponse(
            componentId = componentId,
            description = description,
            status = status
        )
        component.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        component.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            componentRetrieveHandler.getComponent(
                workspaceId = "story",
                resourceId = resourceId,
                componentId = componentId,
            )
        } returns component

        // when
        val exchange = webTestClient.get()
            .uri("/v1/resources/{resourceId}/components/{componentId}", resourceId.code, componentId)
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "component.get",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("resourceId").description("Resource Id"),
                        RequestDocumentation.parameterWithName("componentId").description("Component Id"),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.componentId")
                            .type(JsonFieldType.STRING).description("Component Id"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("Component Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("Component Description"),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                    )
                )
            )
    }

    "컴포넌트 목록을 조회한다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "story user following system"
        val status = ComponentStatus.ENABLED
        val cursor = "cursor"
        val pageSize = 10

        val component = ComponentApiResponse(
            componentId = componentId,
            description = description,
            status = status
        )
        component.createdAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)
        component.updatedAt = LocalDateTime.of(2023, 1, 1, 0, 0, 0)

        coEvery {
            componentRetrieveHandler.listComponents(
                workspaceId = any(),
                resourceId = resourceId,
                cursorRequest = any(),
            )
        } returns ComponentListApiResponse(
            components = listOf(component),
            cursor = Cursor(
                nextCursor = "nextCursor",
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/{resourceId}/components?cursor={cursor}&pageSize={pageSize}",
                resourceId.code, cursor, pageSize
            )
            .headers(WebClientUtils.authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "component.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("resourceId").description("Resource Id"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("Cursor").optional()
                            .attributes(RestDocsUtils.remarks("first cursor is null")),
                        RequestDocumentation.parameterWithName("pageSize").description("Page Size")
                            .attributes(RestDocsUtils.remarks("max: 50")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("result"),
                        PayloadDocumentation.fieldWithPath("result.components")
                            .type(JsonFieldType.ARRAY).description("component list"),
                        PayloadDocumentation.fieldWithPath("result.components[].componentId")
                            .type(JsonFieldType.STRING).description("Component Id"),
                        PayloadDocumentation.fieldWithPath("result.components[].status")
                            .type(JsonFieldType.STRING).description("Component Status")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.components[].description")
                            .type(JsonFieldType.STRING).description("Component Description"),
                        PayloadDocumentation.fieldWithPath("result.components[].createdAt")
                            .type(JsonFieldType.STRING).description("CreatedAt"),
                        PayloadDocumentation.fieldWithPath("result.components[].updatedAt")
                            .type(JsonFieldType.STRING).description("UpdatedAt"),
                        PayloadDocumentation.fieldWithPath("result.cursor")
                            .type(JsonFieldType.OBJECT).description("Page Cursor"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .attributes(RestDocsUtils.remarks("if no more return null"))
                            .type(JsonFieldType.STRING).description("Next Page Cursor").optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("Has More Page (next direction)"),
                    )
                )
            )
    }

})
