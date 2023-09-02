package com.story.platform.core.domain.reaction

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReactionRepository : CoroutineCrudRepository<Reaction, ReactionPrimaryKey>
