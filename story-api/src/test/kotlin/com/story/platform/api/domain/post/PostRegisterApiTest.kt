package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.AuthContextMethodArgumentResolver
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.post.PostRegisterHandler
import com.story.platform.core.domain.post.PostSpaceKey
import com.story.platform.core.domain.post.PostSpaceType
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    PostRegisterApi::class,
    AuthContextMethodArgumentResolver::class,
)
class PostRegisterApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRegisterHandler: PostRegisterHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            serviceType = ServiceType.TWEETER,
            apiKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
        )
    }

    test("새로운 포스트를 등록한다") {
        // given
        val spaceId = "계정의 ID"
        val spaceType = PostSpaceType.ACCOUNT

        val request = PostRegisterApiRequest(
            accountId = spaceId,
            title = "토끼가 너무 좋아요",
            content = """
                    내가 만든 쿠키~
                    너를 위해 구워찌이
            """.trimIndent()
        )

        coEvery {
            postRegisterHandler.register(
                postSpaceKey = PostSpaceKey(
                    serviceType = ServiceType.TWEETER,
                    spaceId = spaceId,
                    spaceType = spaceType,
                ),
                accountId = spaceId,
                title = request.title,
                content = request.content,
                extraJson = request.extraJson,
            )
        } returns 1

        // when
        val exchange = webTestClient.post()
            .uri("/v1/spaces/{spaceType}/{spaceId}/posts", spaceType, spaceId)
            .headers(WebClientUtils.commonHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result.postId").isEqualTo("1")
    }

})
