package com.story.core.domain.apikey

import com.story.core.support.cassandra.CassandraBasicRepository

interface ApiKeyCassandraRepository : CassandraBasicRepository<ApiKeyEntity, String>
