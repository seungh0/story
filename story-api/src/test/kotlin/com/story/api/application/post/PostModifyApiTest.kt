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
import com.story.api.lib.isTrue
import com.story.core.domain.post.PostKey
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
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(PostModifyApi::class)
class PostModifyApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postModifyHandler: PostModifyHandler,
) : FunSpecDocsTest({

    test("기존 포스트를 수정합니다") {
        // given
        val componentId = "user-post"
        val postId = 7126L
        val spaceId = "user-space-id"
        val postKey = PostKey(spaceId = spaceId, depth = 1, parentId = null, postId = postId)

        val request = PostModifyApiRequest(
            title = "플랫폼 정보",
            sections = listOf(
                PostSectionApiRequest(
                    sectionType = PostSectionType.TEXT.name,
                    data = mapOf(
                        "priority" to 1L,
                        "content" to "포스트 내용",
                        "extra" to emptyMap<String, Any>(),
                    )
                ),
                PostSectionApiRequest(
                    sectionType = PostSectionType.IMAGE.name,
                    data = mapOf(
                        "priority" to 2L,
                        "fileId" to 12345123,
                        "extra" to emptyMap<String, Any>(),
                    )
                ),
                PostSectionApiRequest(
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
        )

        coEvery {
            postModifyHandler.patchPost(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "story",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                postId = postKey,
                ownerId = any(),
                title = request.title,
                sections = request.toSections(),
            )
        } returns Unit

        // when
        val exchange = webTestClient.patch()
            .uri(
                "/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}",
                componentId,
                spaceId,
                postKey.serialize(),
            )
            .headers(WebClientUtils.apiKeyHeaderWithRequestUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.ok").isTrue()
            .consumeWith(
                document(
                    "post.modify",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderWithRequestUserIdDocumentation,
                    pathParameters(
                        parameterWithName("componentId").description("포스트 컴포넌트 ID"),
                        parameterWithName("spaceId").description("포스트 공간 ID"),
                        parameterWithName("postId").description("포스트 ID"),
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
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                    )
                )
            )
    }

})
