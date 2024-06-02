package com.story.core.domain.policy

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface ApiLimitPolicyCassandraRepository : CassandraBasicRepository<ApiLimitPolicyEntity, ApiLimitPolicyPrimaryKey>
