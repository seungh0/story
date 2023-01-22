package com.story.datacenter.api.domain

import com.story.datacenter.api.RestDocsTest
import com.story.datacenter.api.lib.RestDocsUtils.getDocumentRequest
import com.story.datacenter.api.lib.RestDocsUtils.getDocumentResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document

@WebFluxTest(HealthController::class)
internal class HealthControllerApiDocsTest : RestDocsTest() {

    @Test
    fun `Health Check API Test`() {
        webClient.get()
            .uri("/health")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo("OK")
            .consumeWith(
                document(
                    "HEALTH-CHECK",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestParameters(),
                    responseFields(
                        fieldWithPath("result").type(JsonFieldType.STRING).description("Result")
                    )
                )
            )
    }

}
