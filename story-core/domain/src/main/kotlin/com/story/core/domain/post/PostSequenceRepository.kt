package com.story.core.domain.post

interface PostSequenceRepository {

    suspend fun generatePostNo(postSpaceKey: PostSpaceKey, parentId: PostId?): Long

    suspend fun set(postSpaceKey: PostSpaceKey, parentId: PostId?, value: Long)

    suspend fun del(postSpaceKey: PostSpaceKey, parentId: PostId?)

    suspend fun getLastSequence(postSpaceKey: PostSpaceKey, parentId: PostId?): Long

}
