package com.story.platform.core.domain.post

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostRemoverTest(
    private val postRemover: PostRemover,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
) : FunSpec({

    afterEach {
        postRepository.deleteAll()
        postReverseRepository.deleteAll()
    }

    context("등록된 포스트를 삭제한다") {
        test("기존에 등록된 포스트를 삭제한다") {
            // given
            val post = PostFixture.create()
            postRepository.save(post)

            val postReverse = PostReverse.of(post)
            postReverseRepository.save(postReverse)

            // when
            postRemover.remove(
                postSpaceKey = PostSpaceKey(
                    serviceType = post.key.serviceType,
                    spaceType = post.key.spaceType,
                    spaceId = post.key.spaceId,
                ),
                postId = post.key.postId,
                accountId = post.accountId,
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 0

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 0
        }

        test("등록되지 않은 포스트를 삭제하는 경우 멱등성을 갖는다") {
            // when
            postRemover.remove(
                postSpaceKey = PostSpaceKey(
                    serviceType = ServiceType.TWEETER,
                    spaceType = PostSpaceType.POST_COMMENT,
                    spaceId = "포스트 작성 공간 ID",
                ),
                postId = -1L,
                accountId = "포스트 작성자 ID",
            )

            // then
            val posts = postRepository.findAll().toList()
            posts shouldHaveSize 0

            val postReverses = postReverseRepository.findAll().toList()
            postReverses shouldHaveSize 0
        }
    }

})
