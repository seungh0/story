package com.story.api.application.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils
import com.story.core.domain.authentication.AuthenticationStatus
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
) : StringSpecDocsTest({

    "인증 키에 대한 정보를 변경합니다" {
        // given
        val authenticationKey = UUID.randomUUID().toString()
        val description = "Story Platform에서 사용할 인증 키"
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
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("authenticationKey").description("인증 키"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("인증 정보에 대한 설명")
                            .attributes(RestDocsUtils.remarks("최대 300자까지 사용할 수 있습니다"))
                            .optional(),
                        PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING)
                            .description("인증 키에 대한 상태 값")
                            .attributes(RestDocsUtils.remarks(RestDocsUtils.convertToString(AuthenticationStatus::class.java)))
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
