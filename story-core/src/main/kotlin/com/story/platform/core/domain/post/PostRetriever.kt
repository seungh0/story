package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.CursorDirection
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.error.BadRequestException
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.common.error.NotFoundException
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.common.model.CursorRequest
import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.support.coroutine.CoroutineConfig
import com.story.platform.core.support.coroutine.IOBound
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class PostRetriever(
    private val postRepository: PostRepository,
    private val postSequenceGenerator: PostSequenceGenerator,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun getPost(
        postSpaceKey: PostSpaceKey,
        postId: Long,
    ): Post {
        return postRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        ) ?: throw NotFoundException("해당하는 Space($postSpaceKey)에 포스트($postId)가 존재하지 않습니다")
    }

    suspend fun listPosts(
        serviceType: ServiceType,
        keys: Collection<PostKey>,
    ): List<Post> {
        return withContext(dispatcher) {
            val posts = keys.map { key ->
                async {
                    withTimeout(CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                        try {
                            postRepository.findById(
                                PostPrimaryKey.of(
                                    postSpaceKey = PostSpaceKey(
                                        serviceType = serviceType,
                                        spaceId = key.spaceId,
                                        spaceType = key.spaceType,
                                    ),
                                    postId = key.postId,
                                )
                            )
                        } catch (exception: TimeoutCancellationException) {
                            throw InternalServerException(exception.message ?: "Coroutine Timeout Exception", exception)
                        }
                    }
                }
            }

            return@withContext posts.awaitAll().filterNotNull()
        }
    }

    suspend fun listPosts(
        postSpaceKey: PostSpaceKey,
        cursorRequest: CursorRequest,
    ): CursorResult<Post, String> {
        val (posts, slot: Long) = when (cursorRequest.direction) {
            CursorDirection.NEXT -> listNextPosts(cursorRequest, postSpaceKey)
            CursorDirection.PREVIOUS -> listPreviousPosts(cursorRequest, postSpaceKey)
        }

        if (posts.size > cursorRequest.pageSize) {
            return CursorResult(
                data = posts.subList(0, cursorRequest.pageSize.coerceAtMost(posts.size)),
                cursor = getCursor(posts = posts, pageSize = cursorRequest.pageSize),
            )
        }

        val morePosts = when (cursorRequest.direction) {
            CursorDirection.NEXT -> {
                postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
                    serviceType = postSpaceKey.serviceType,
                    spaceType = postSpaceKey.spaceType,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot - 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).content
            }

            CursorDirection.PREVIOUS -> {
                postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                    serviceType = postSpaceKey.serviceType,
                    spaceType = postSpaceKey.spaceType,
                    spaceId = postSpaceKey.spaceId,
                    slotId = slot + 1,
                    pageable = CassandraPageRequest.first(cursorRequest.pageSize - posts.size + 1),
                ).content
            }
        }

        val data = posts + morePosts.subList(0, (cursorRequest.pageSize - posts.size).coerceAtMost(morePosts.size))

        return CursorResult(
            data = data,
            cursor = getCursor(posts = morePosts, pageSize = cursorRequest.pageSize - posts.size),
        )
    }

    private suspend fun listNextPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<List<Post>, Long> {
        if (cursorRequest.cursor == null) {
            val lastSlotId =
                PostSlotAssigner.assign(postId = postSequenceGenerator.lastSequence(postSpaceKey = postSpaceKey))
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotId(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = lastSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).content to lastSlotId
        }

        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다")
        )
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdLessThan(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        ).content to currentSlot
    }

    private suspend fun listPreviousPosts(
        cursorRequest: CursorRequest,
        postSpaceKey: PostSpaceKey,
    ): Pair<List<Post>, Long> {
        if (cursorRequest.cursor == null) {
            val firstSlotId = PostSlotAssigner.FIRST_SLOT_ID
            return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdOrderByKeyPostIdAsc(
                serviceType = postSpaceKey.serviceType,
                spaceType = postSpaceKey.spaceType,
                spaceId = postSpaceKey.spaceId,
                slotId = firstSlotId,
                pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
            ).content to firstSlotId
        }
        val currentSlot = PostSlotAssigner.assign(
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
        )
        return postRepository.findAllByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostIdGreaterThanOrderByKeyPostIdAsc(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = currentSlot,
            postId = cursorRequest.cursor.toLongOrNull()
                ?: throw BadRequestException("잘못된 Cursor(${cursorRequest.cursor})입니다"),
            pageable = CassandraPageRequest.first(cursorRequest.pageSize + 1),
        ).content to currentSlot
    }

    private fun getCursor(posts: List<Post>, pageSize: Int): Cursor<String> {
        if (posts.size > pageSize) {
            return Cursor.of(
                cursor = posts.subList(0, pageSize.coerceAtMost(posts.size)).lastOrNull()?.key?.postId?.toString()
            )
        }
        return Cursor.of(cursor = null)
    }

}
