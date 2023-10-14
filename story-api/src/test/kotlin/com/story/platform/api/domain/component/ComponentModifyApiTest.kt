package com.story.platform.api.domain.component

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.convertToString
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.resource.ResourceId
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(ComponentModifyApi::class)
class ComponentModifyApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val componentModifyHandler: ComponentModifyHandler,

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

    "컴포넌트 정보를 수정합니다" {
        // given
        val resourceId = ResourceId.SUBSCRIPTIONS
        val componentId = "user-follow"
        val description = "[Story Platform] 유저 팔로우 시스템"
        val status = ComponentStatus.ENABLED

        val request = ComponentModifyApiRequest(
            description = description,
            status = status,
        )

        coEvery {
            componentModifyHandler.patchComponent(
                workspaceId = any(),
                resourceId = resourceId,
                componentId = componentId,
                description = description,
                status = status,
            )
        } returns ComponentResponse(
            componentId = componentId,
            description = description,
            status = ComponentStatus.ENABLED,
        )

        // when
        val exchange = webTestClient.patch()
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
                    "component.modify",
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
                            .attributes(remarks("최대 300자까지 사용할 수 있습니다"))
                            .optional(),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("컴포넌트에 대한 상태 값")
                            .attributes(remarks(convertToString(ComponentStatus::class.java)))
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
