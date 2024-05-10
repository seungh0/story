package com.story.core.domain.policy

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ApiLimitPolicyRepository : CassandraBasicRepository<ApiLimitPolicyEntity, ApiLimitPolicyPrimaryKey>
