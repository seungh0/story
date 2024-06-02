package com.story.core.domain.apikey.storage

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ApiKeyCassandraRepository : CassandraBasicRepository<ApiKeyEntity, String>
