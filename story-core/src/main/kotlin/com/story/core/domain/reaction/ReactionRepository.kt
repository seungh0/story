package com.story.core.domain.reaction

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ReactionRepository : CassandraBasicRepository<Reaction, ReactionPrimaryKey>
