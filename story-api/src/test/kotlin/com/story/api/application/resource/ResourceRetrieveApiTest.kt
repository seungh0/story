package com.story.api.application.resource

import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.StringSpecDocsTest
import com.story.api.lib.PageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.WebClientUtils.authenticationHeader
import com.story.api.lib.isTrue
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(ResourceRetrieveApi::class, ResourceRetrieveHandler::class)
class ResourceRetrieveApiTest(
    private val webTestClient: WebTestClient,
) : StringSpecDocsTest({

    "사용 가능한 리소스 목록을 조회한다" {
        // given
        val pageSize = 10

        // when
        val exchange = webTestClient.get()
            .uri("/v1/resources?pageSize={pageSize}", pageSize)
            .headers(authenticationHeader)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                WebTestClientRestDocumentation.document(
                    "resource.list",
                    RestDocsUtils.getDocumentRequest(),
                    RestDocsUtils.getDocumentResponse(),
                    PageHeaderSnippet.pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderDocumentation,
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("pageSize").description("조회할 갯수")
                            .attributes(RestDocsUtils.remarks("최대 50개까지 조회할 수 있습니다")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.resources[]")
                            .type(JsonFieldType.ARRAY).description("리소스 목록"),
                        fieldWithPath("result.resources[].resourceId")
                            .type(JsonFieldType.STRING).description("리소스 ID"),
                        fieldWithPath("result.resources[].description")
                            .type(JsonFieldType.STRING).description("리소스에 대한 설명"),
                    )
                )
            )
    }

})
