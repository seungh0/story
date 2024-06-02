package com.story.core.domain.post

import com.story.core.IntegrationTest
import com.story.core.StringSpecIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class PostRepositoryTest(
    private val postRepository: PostRepository,
) : StringSpecIntegrationTest({

    "Post의 메타 정보를 변경한다 - 없던 경우 신규로 추가된다" {
        // given
        val post = PostFixture.create()
        postRepository.save(post)

        // when
        postRepository.putMetadata(
            key = post.key,
            metadataType = PostMetadataType.HAS_CHILDREN,
            value = true.toString(),
        )

        // then
        val posts = postRepository.findAll().toList()
        posts shouldHaveSize 1
        posts[0].key shouldBe post.key
        posts[0].metadata shouldBe mutableMapOf(PostMetadataType.HAS_CHILDREN to "true")
    }

    "Post의 메타 정보를 변경한다 - 이미 있던 경우 변경된다" {
        // given
        val post = PostFixture.create(
            metadata = mapOf(PostMetadataType.HAS_CHILDREN to false.toString())
        )
        postRepository.save(post)

        // when
        postRepository.putMetadata(
            key = post.key,
            metadataType = PostMetadataType.HAS_CHILDREN,
            value = true.toString(),
        )

        // then
        val posts = postRepository.findAll().toList()
        posts shouldHaveSize 1
        posts[0].key shouldBe post.key
        posts[0].metadata shouldBe mutableMapOf(PostMetadataType.HAS_CHILDREN to "true")
    }

})
