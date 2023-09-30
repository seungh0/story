package com.story.platform.core.domain.post

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.model.CursorDirection
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.lib.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class PostRetrieverTest(
    private val postRepository: PostRepository,
    private val postReverseRepository: PostReverseRepository,
    private val postRetriever: PostRetriever,
    private val testCleaner: TestCleaner,
    private val postSequenceRepository: PostSequenceRepository,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("특정 공간에 등록되어 있는 포스트 목록을 조회한다 - NEXT 방향 조회") {
        context("첫 페이지 단일 슬롯 조회") {
            test("첫 페이지 조회 with 다음 커서") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 9
                sut.data[1].postId shouldBe 8
                sut.data[2].postId shouldBe 7

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "7"
            }

            test("첫 페이지 조회 without (pageSize == contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..3).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 3
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 1

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }

            test("첫 페이지 조회 without 다음 커서 (pageSize > contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..3).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.NEXT,
                        pageSize = 4,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 3
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 1

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("첫 페이지 멀티 슬롯 조회") {
            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9998..10001).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 10001
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 9999

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "9999"
            }

            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다 (without 다음 커서)") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9999..10001).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 10001
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 9999

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("커서 기준으로 단일 슬롯 조회") {
            test("커서 기준으로 페이지 조회 with 다음 커서") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "7",
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 6
                sut.data[1].postId shouldBe 5
                sut.data[2].postId shouldBe 4

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "4"
            }

            test("커서 기준으로 페이지 조회 without 다음 커서 (pageSize == contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "4",
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 3
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 1

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }

            test("커서 기준으로 페이지 조회 without 다음 커서 (pageSize > contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "4",
                        direction = CursorDirection.NEXT,
                        pageSize = 4,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 3
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 1

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("커서 기준 멀티 슬롯 조회") {
            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9998..10002).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "10002",
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 10001
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 9999

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "9999"
            }

            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다 (without 다음 커서)") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9999..10002).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "10002",
                        direction = CursorDirection.NEXT,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 10001
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 9999

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }
    }

    context("특정 공간에 등록되어 있는 포스트 목록을 조회한다 - PREVIOUS 방향 조회") {
        context("첫 페이지 단일 슬롯 조회") {
            test("첫 페이지 조회 with 이전 커서") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 1
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 3

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "3"
            }

            test("첫 페이지 조회 without (pageSize == contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..3).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 1
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 3

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }

            test("첫 페이지 조회 without 다음 커서 (pageSize > contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..3).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 4,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 1
                sut.data[1].postId shouldBe 2
                sut.data[2].postId shouldBe 3

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("첫 페이지 멀티 슬롯 조회") {
            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9999..10002).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 9999
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 10001

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "10001"
            }

            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다 (without 다음 커서)") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9999..10001).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = null,
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 9999
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 10001

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("커서 기준으로 단일 슬롯 조회") {
            test("커서 기준으로 페이지 조회 with 다음 커서") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "2",
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 3
                sut.data[1].postId shouldBe 4
                sut.data[2].postId shouldBe 5

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "5"
            }

            test("커서 기준으로 페이지 조회 without 다음 커서 (pageSize == contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "6",
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 7
                sut.data[1].postId shouldBe 8
                sut.data[2].postId shouldBe 9

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }

            test("커서 기준으로 페이지 조회 without 다음 커서 (pageSize > contents)") {
                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (1..9).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "6",
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 4,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 7
                sut.data[1].postId shouldBe 8
                sut.data[2].postId shouldBe 9

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }

        context("커서 기준 멀티 슬롯 조회") {
            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9998..10002).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "9998",
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 9999
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 10001

                sut.cursor.hasNext shouldBe true
                sut.cursor.nextCursor shouldBe "10001"
            }

            test("현재 슬롯에서 더 이상 포스트가 없는 경우, 이전 슬롯의 처음부터 조회한다 (without 다음 커서)") {
                // given
                postSequenceRepository.set(postSpaceKey = POST_SPACE_KEY, count = 10001L)

                val posts = mutableListOf<Post>()
                val postReverses = mutableListOf<PostReverse>()
                (9998..10001).forEach { postId ->
                    val post = PostFixture.create(
                        workspaceId = POST_SPACE_KEY.workspaceId,
                        componentId = POST_SPACE_KEY.componentId,
                        spaceId = POST_SPACE_KEY.spaceId,
                        postId = postId.toLong(),
                    )
                    posts += post
                    postReverses += PostReverse.of(post)
                }
                postRepository.saveAll(posts).toList()
                postReverseRepository.saveAll(postReverses).toList()

                // when
                val sut = postRetriever.listPosts(
                    postSpaceKey = POST_SPACE_KEY,
                    cursorRequest = CursorRequest(
                        cursor = "9998",
                        direction = CursorDirection.PREVIOUS,
                        pageSize = 3,
                    ),
                    sortBy = PostSortBy.LATEST,
                )

                // then
                sut.data shouldHaveSize 3
                sut.data[0].postId shouldBe 9999
                sut.data[1].postId shouldBe 10000
                sut.data[2].postId shouldBe 10001

                sut.cursor.hasNext shouldBe false
                sut.cursor.nextCursor shouldBe null
            }
        }
    }
}) {

    companion object {
        val POST_SPACE_KEY = PostSpaceKey(
            workspaceId = "workspaceId",
            componentId = "post",
            spaceId = "account-id",
        )
    }

}
