package com.story.api.application.component

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.common.model.dto.CursorResponse
import com.story.core.domain.component.ComponentStatus
import com.story.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.UUID

@DocsTest
@ApiTest(ComponentRetrieveApi::class)
class ComponentRetrieveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val componentRetrieveHandler: ComponentRetrieveHandler,
) : StringSpecDocsTest({

    "컴포넌트를 조회합니다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "[Story Platform] 유저 팔로우 시스템"
        val status = ComponentStatus.ENABLED

        val request = ComponentGetRequest(
            filterStatus = ComponentStatus.ENABLED,
        )

        val component = ComponentResponse(
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
                request = request,
            )
        } returns component

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/{resourceId}/components/{componentId}?filterStatus={filterStatus}",
                resourceId.code,
                componentId,
                request.filterStatus
            )
            .headers(WebClientUtils.apiKeyHeader)
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
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("resourceId").description("리소스 ID"),
                        RequestDocumentation.parameterWithName("componentId").description("컴포넌트 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("filterStatus")
                            .description("필터링 상태 값 (null인 경우 상태 값에 상관 없이 조회)")
                            .optional()
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.componentId")
                            .type(JsonFieldType.STRING).description("컴포넌트 Id"),
                        PayloadDocumentation.fieldWithPath("result.status")
                            .type(JsonFieldType.STRING).description("컴포넌트 상태 값")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.description")
                            .type(JsonFieldType.STRING).description("컴포넌트에 대한 설명"),
                        PayloadDocumentation.fieldWithPath("result.createdAt")
                            .type(JsonFieldType.STRING).description("컴포넌트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.updatedAt")
                            .type(JsonFieldType.STRING).description("컴포넌트 최근 수정 일자"),
                    )
                )
            )
    }

    "컴포넌트 목록을 조회한다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "[Story Platform] 유저 팔로우 시스템"
        val status = ComponentStatus.ENABLED
        val cursor = UUID.randomUUID().toString()
        val pageSize = 10

        val component = ComponentResponse(
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
                request = any(),
            )
        } returns ComponentListResponse(
            components = listOf(component),
            cursor = CursorResponse(
                nextCursor = UUID.randomUUID().toString(),
                hasNext = true,
            )
        )

        // when
        val exchange = webTestClient.get()
            .uri(
                "/v1/resources/{resourceId}/components?cursor={cursor}&pageSize={pageSize}",
                resourceId.code, cursor, pageSize
            )
            .headers(WebClientUtils.apiKeyHeader)
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
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("resourceId").description("리소스 ID"),
                    ),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("cursor").description("페이지 커서").optional()
                            .attributes(RestDocsUtils.remarks("첫 페이지의 경우 null")),
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지 조회할 수 있습니다")),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        PayloadDocumentation.fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        PayloadDocumentation.fieldWithPath("result.components")
                            .type(JsonFieldType.ARRAY).description("컴포넌트 목록"),
                        PayloadDocumentation.fieldWithPath("result.components[].componentId")
                            .type(JsonFieldType.STRING).description("컴포넌트 Id"),
                        PayloadDocumentation.fieldWithPath("result.components[].status")
                            .type(JsonFieldType.STRING).description("컴포넌트 상태 값")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(ComponentStatus::class.java))),
                        PayloadDocumentation.fieldWithPath("result.components[].description")
                            .type(JsonFieldType.STRING).description("컴포넌트에 대한 설명"),
                        PayloadDocumentation.fieldWithPath("result.components[].createdAt")
                            .type(JsonFieldType.STRING).description("컴포넌트 생성 일자"),
                        PayloadDocumentation.fieldWithPath("result.components[].updatedAt")
                            .type(JsonFieldType.STRING).description("컴포넌트 최근 수정 일자"),
                        PayloadDocumentation.fieldWithPath("result.cursor")
                            .type(JsonFieldType.OBJECT).description("페이지 커서 정보"),
                        PayloadDocumentation.fieldWithPath("result.cursor.nextCursor")
                            .type(JsonFieldType.STRING).description("다음 페이지를 조회하기 위한 커서")
                            .attributes(RestDocsUtils.remarks("다음 페이지가 없는 경우 null"))
                            .optional(),
                        PayloadDocumentation.fieldWithPath("result.cursor.hasNext")
                            .type(JsonFieldType.BOOLEAN).description("다음 페이지의 존재 여부)"),
                    )
                )
            )
    }

})
