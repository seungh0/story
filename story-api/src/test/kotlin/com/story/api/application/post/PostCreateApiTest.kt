package com.story.api.application.post

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.RestDocsUtils.remarks
import com.story.api.lib.WebClientUtils
import com.story.core.domain.nonce.NonceManager
import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.post.section.PostSectionType
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

        val request = PostCreateRequest(
            parentId = null,
            title = "플랫폼 정보",
            sections = listOf(
                PostSectionRequest(
                    sectionType = PostSectionType.TEXT.name,
                    data = mapOf(
                        "priority" to 1L,
                        "content" to "포스트 내용",
                        "extra" to emptyMap<String, Any>(),
                    )
                ),
                PostSectionRequest(
                    sectionType = PostSectionType.IMAGE.name,
                    data = mapOf(
                        "priority" to 2L,
                        "fileId" to 12345123,
                        "extra" to emptyMap<String, Any>(),
                    )
                ),
                PostSectionRequest(
                    sectionType = PostSectionType.LINK.name,
                    data = mapOf(
                        "priority" to 3L,
                        "link" to "https://intro.threedollars.co.kr",
                        "extra" to mapOf(
                            "og:image" to "http://localhost:5000/abc.png",
                            "og:title" to "뽀미 토키",
                            "og:description" to "뽀미랑 토키의 사진입니다",
                        )
                    )
                )
            ),
            extra = emptyMap(),
        )

        coEvery {
            postCreateHandler.createPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "story",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                ownerId = any(),
                request = any(),
                nonce = any(),
            )
        } returns PostId(spaceId = spaceId, parentId = null, postNo = 100, depth = 2)

        // when
        val exchange = webTestClient.post()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts?nonce={nonce}",
                componentId,
                spaceId,
                nonce,
            )
            .headers(WebClientUtils.apiKeyHeaderWithRequestUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "post.create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderWithRequestUserIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        parameterWithName("spaceId").description("포스트 공간 ID"),
                    ),
                    queryParameters(
                        parameterWithName("nonce").description("논스 토큰 (Nonce Create API로 생성)").optional(),
                    ),
                    requestFields(
                        fieldWithPath("parentId").type(JsonFieldType.STRING)
                            .description("부모 포스트 ID")
                            .optional(),
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
                        fieldWithPath("sections[].data.extra").type(JsonFieldType.OBJECT)
                            .description("부가적으로 사용할 필드").optional(),
                        fieldWithPath("sections[].data.content").type(JsonFieldType.STRING)
                            .description("[TEXT 섹션 전용] 섹션 내용")
                            .attributes(remarks("최대 500자까지 사용할 수 있습니다")).optional(),
                        fieldWithPath("sections[].data.fileId").type(JsonFieldType.NUMBER)
                            .description("[IMAGE 섹션 전용] 이미지 파일 ID").optional(),
                        fieldWithPath("sections[].data.link").type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] Link").optional(),
                        fieldWithPath("sections[].data.extra.og:image").type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (image)").optional(),
                        fieldWithPath("sections[].data.extra.og:title").type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (title)").optional(),
                        fieldWithPath("sections[].data.extra.og:description").type(JsonFieldType.STRING)
                            .description("[LINK 섹션 전용] OG 태그 (description)").optional(),
                        fieldWithPath("extra").type(JsonFieldType.OBJECT)
                            .description("포스트 추가 정보").optional(),
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
