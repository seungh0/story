package com.story.platform.core.domain.post

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class PostRemoverTest(
    private val postRemover: PostRemover,
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
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
                    workspaceId = post.key.workspaceId,
                    componentId = post.key.componentId,
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
                    workspaceId = "twitter",
                    componentId = "post",
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
