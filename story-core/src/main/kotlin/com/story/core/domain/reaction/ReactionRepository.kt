package com.story.core.domain.reaction

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReactionRepository : CoroutineCrudRepository<Reaction, ReactionPrimaryKey>
