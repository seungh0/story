package com.story.platform.core.domain.reaction

import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactionCountRepository : CoroutineCrudRepository<ReactionCount, ReactionCountPrimaryKey> {

    @Query(
        """
			update reaction_count_v1 set count = count + :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
		    and space_id = :#{#key.spaceId}
			and emotion_id = :#{#key.emotionId}
		"""
    )
    suspend fun increase(key: ReactionCountPrimaryKey, count: Long = 1L)

    @Query(
        """
			update reaction_count_v1 set count = count - :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and space_id = :#{#key.spaceId}
			and emotion_id = :#{#key.emotionId}
		"""
    )
    suspend fun decrease(key: ReactionCountPrimaryKey, count: Long = 1L)

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyEmotionIdIn(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        emotionIds: Collection<String>,
    ): List<ReactionCount>

}
