package com.story.platform.api.application.post

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
import com.story.platform.core.domain.post.section.PostSectionType
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
            sections = listOf(
                PostSectionApiRequest(
                    sectionType = PostSectionType.TEXT.name,
                    data = mapOf(
                        "priority" to 1L,
                        "content" to "포스트 내용",
                    )
                ),
                PostSectionApiRequest(
                    sectionType = PostSectionType.IMAGE.name,
                    data = mapOf(
                        "priority" to 2L,
                        "path" to "/store/v1/store.png",
                        "fileName" to "store.png",
                        "width" to 480,
                        "height" to 360,
                        "fileSize" to 1234123
                    )
                )
            )
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
                sections = request.toSections(),
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
                        fieldWithPath("sections").type(JsonFieldType.ARRAY)
                            .description("포스트 내용 섹션 목록"),
                        fieldWithPath("sections[].sectionType").type(JsonFieldType.STRING)
                            .description("포스트 섹션 타입")
                            .attributes(remarks(RestDocsUtils.convertToString(PostSectionType::class.java))),
                        fieldWithPath("sections[].data.priority").type(JsonFieldType.NUMBER)
                            .description("포스트 섹션 순서")
                            .attributes(remarks("priority가 낮은 것 부터 먼저 조회됩니다")),
                        fieldWithPath("sections[].data.content").type(JsonFieldType.STRING)
                            .description("[TEXT 섹션 전용] 섹션 내용")
                            .attributes(remarks("최대 500자까지 사용할 수 있습니다")).optional(),
                        fieldWithPath("sections[].data.path").type(JsonFieldType.STRING)
                            .description("[IMAGE 섹션 전용] 이미지 Path").optional(),
                        fieldWithPath("sections[].data.fileName").type(JsonFieldType.STRING)
                            .description("[IMAGE 섹션 전용] 이미지 파일 이름").optional(),
                        fieldWithPath("sections[].data.width").type(JsonFieldType.NUMBER)
                            .description("[IMAGE 섹션 전용] 이미지 가로 길이").optional(),
                        fieldWithPath("sections[].data.height").type(JsonFieldType.NUMBER)
                            .description("[IMAGE 섹션 전용] 이미지 세로 길이").optional(),
                        fieldWithPath("sections[].data.fileSize").type(JsonFieldType.NUMBER)
                            .description("[IMAGE 섹션 전용] 이미지 파일 사이즈").optional(),
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
