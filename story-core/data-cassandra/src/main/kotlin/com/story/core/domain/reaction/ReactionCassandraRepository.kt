package com.story.core.domain.reaction

import com.story.core.support.cassandra.CassandraBasicRepository

interface ReactionCassandraRepository : CassandraBasicRepository<ReactionEntity, ReactionPrimaryKey>
