package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.FunSpecDocsTest
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.nonce.NonceManager
import com.story.platform.core.domain.post.PostSpaceKey
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(PostCreateApi::class)
class PostCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postCreateHandler: PostCreateHandler,
    @MockkBean
    private val nonceManager: NonceManager,
) : FunSpecDocsTest({

    beforeEach {
        coEvery { nonceManager.verify(any()) } returns Unit
    }

    test("새로운 포스트를 등록한다") {
        // given
        val componentId = "user-post"
        val spaceId = "user-space-id"
        val nonce = UUID.randomUUID().toString()

        val request = PostCreateApiRequest(
            title = "플랫폼 정보",
            content = """
                   1. 스토리 플랫폼(Story Platform)이란?
                   - "스토리 플랫폼"은 비즈니스를 위한 서비스를 쉽게 개발할 수 있도록 컴포넌트 플랫폼을 제공합니다.

                   2. 누구를 위한 플랫폼 인가요?
                   - 제품을 신속하게 출시하고 시장 응답을 확인하고자 하는 스타트업 또는 팀.
                   - 서버 개발과 관리의 비용과 수고를 들이지 않고 비즈니스를 위한 서비스를 만들고자 하는 개인 또는 팀.
            """.trimIndent(),
        )

        coEvery {
            postCreateHandler.createPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "story",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                accountId = any(),
                title = request.title,
                content = request.content,
                nonce = any(),
            )
        } returns 1

        // when
        val exchange = webTestClient.post()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts?nonce={nonce}",
                componentId,
                spaceId,
                nonce,
            )
            .headers(WebClientUtils.authenticationHeaderWithRequestAccountId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result.postId").isEqualTo("1")
            .consumeWith(
                document(
                    "post.create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.authenticationHeaderWithRequestAccountIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        parameterWithName("spaceId").description("포스트 공간 ID"),
                    ),
                    queryParameters(
                        parameterWithName("nonce").description("논스 토큰 (Nonce Create API로 생성)").optional(),
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING)
                            .description("포스트 제목")
                            .attributes(remarks("최대 100자까지 사용할 수 있습니다")),
                        fieldWithPath("content").type(JsonFieldType.STRING)
                            .description("포스트 내용")
                            .attributes(remarks("최대 500자까지 사용할 수 있습니다")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.postId")
                            .type(JsonFieldType.STRING).description("포스트 ID"),
                    )
                )
            )
    }

})
