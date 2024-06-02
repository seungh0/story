package com.story.api.application.apikey

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.remarks
import com.story.api.lib.WebClientUtils
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(ApiKeyCreateApi::class)
class ApiKeyCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val apiKeyCreateHandler: ApiKeyCreateHandler,
) : StringSpecDocsTest({

    "신규 API-키를 등록합니다" {
        // given
        val key = UUID.randomUUID().toString()
        val description = "Story Platform에서 사용할 API-Key"

        val request = ApiKeyCreateRequest(
            description = description,
        )

        coEvery {
            apiKeyCreateHandler.createApiKey(
                workspaceId = any(),
                apiKey = key,
                description = description,
            )
        } returns Unit

        // when
        val exchange = webTestClient.post()
            .uri("/v1/api-keys/{apiKey}", key)
            .headers(WebClientUtils.apiKeyHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "api-key.create",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("apiKey").description("API 키"),
                    ),
                    PayloadDocumentation.requestFields(
                        PayloadDocumentation.fieldWithPath("description").type(JsonFieldType.STRING)
                            .description("API 키에 대한 설명")
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
