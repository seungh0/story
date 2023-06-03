package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.AccountIdResolver
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.post.PostRemoveHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(PostRemoveApi::class, AccountIdResolver::class)
class PostRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRemoveHandler: PostRemoveHandler,
) : FunSpec({

    test("기존에 등록된 포스트를 삭제한다") {
        // given
        val postSpaceKey = PostSpaceKey(
            serviceType = ServiceType.TWEETER,
            spaceType = PostSpaceType.POST_COMMENT,
            spaceId = "게시글 공간 ID"
        )

        val accountId = "작성자 ID"
        val postId = 30000L

        coEvery {
            postRemoveHandler.remove(
                postSpaceKey = postSpaceKey,
                accountId = accountId,
                postId = postId,
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/spaces/{spaceType}/{spaceId}/posts/{postId}?accountId={accountId}",
                postSpaceKey.spaceType,
                postSpaceKey.spaceId,
                postId,
                accountId
            )
            .headers(WebClientUtils.commonHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(ApiResponse.OK.result!!)
    }

})
