package com.story.platform.api.domain.component

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.StringSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.resource.ResourceId
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(ComponentCreateApi::class)
class ComponentCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val componentCreateHandler: ComponentCreateHandler,
) : StringSpecDocsTest({

    "신규 컴포넌트를 등록합니다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "[Story Platform] 유저 팔로우 시스템"

        val request = ComponentCreateApiRequest(
            description = description,
        )

        coEvery {
            componentCreateHandler.createComponent(
                workspaceId = any(),
                resourceId = resourceId,
                componentId = componentId,
                description = description,
            )
        } returns ComponentResponse(
            componentId = componentId,
            description = description,
            status = ComponentStatus.ENABLED,
        )

        // when
        val exchange = webTestClient.post()
            .uri("/v1/resources/{resourceId}/components/{componentId}", resourceId.code, componentId)
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
                    "component.create",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("resourceId").description("리소스 ID"),
                        RequestDocumentation.parameterWithName("componentId").description("컴포넌트 ID"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("컴포넌트에 대한 설명")
                            .attributes(RestDocsUtils.remarks("최대 300자까지 사용할 수 있습니다"))
                            .optional(),
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
