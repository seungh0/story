package com.story.platform.api.domain.authentication

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.StringSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(AuthenticationCreateApi::class)
class AuthenticationCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val authenticationCreateHandler: AuthenticationCreateHandler,
) : StringSpecDocsTest({

    "신규 인증 키를 등록합니다" {
        // given
        val authenticationKey = UUID.randomUUID().toString()
        val description = "Story Platform에서 사용할 인증 키"

        val request = AuthenticationCreateApiRequest(
            description = description,
        )

        coEvery {
            authenticationCreateHandler.createAuthentication(
                workspaceId = any(),
                authenticationKey = authenticationKey,
                description = description,
            )
        } returns Unit

        // when
        val exchange = webTestClient.post()
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
                    "authentication.create",
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
                            .attributes(remarks("최대 300자까지 사용할 수 있습니다"))
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
