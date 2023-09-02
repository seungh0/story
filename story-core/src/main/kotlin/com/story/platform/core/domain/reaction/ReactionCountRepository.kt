package com.story.platform.core.domain.reaction

interface ReactionCountRepository {

    suspend fun increase(key: ReactionCountKey, count: Long = 1L): Long

    suspend fun increaseBulk(keys: Set<ReactionCountKey>, count: Long = 1L)

    suspend fun decrease(key: ReactionCountKey, count: Long = 1L): Long

    suspend fun decreaseBulk(keys: Set<ReactionCountKey>, count: Long = 1L)

    suspend fun get(key: ReactionCountKey): Long

    suspend fun getBulk(keys: Set<ReactionCountKey>): Map<ReactionCountKey, Long>

    suspend fun delete(key: ReactionCountKey)

    suspend fun deleteBulk(keys: Set<ReactionCountKey>)

}
