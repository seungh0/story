package com.story.core.domain.apikey

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ApiKeyCassandraRepository : CassandraBasicRepository<ApiKeyEntity, String>
