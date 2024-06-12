package com.story.core.domain.policy

import com.story.core.support.cassandra.CassandraBasicRepository

interface ApiLimitPolicyCassandraRepository : CassandraBasicRepository<ApiLimitPolicyEntity, ApiLimitPolicyPrimaryKey>
