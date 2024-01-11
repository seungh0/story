package com.story.core.domain.policy

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ApiLimitPolicyRepository : CoroutineCrudRepository<ApiLimitPolicy, ApiLimitPolicyPrimaryKey>
