package com.story.core.domain.authentication

import com.story.core.infrastructure.cassandra.CassandraBasicRepository

interface AuthenticationRepository : CassandraBasicRepository<Authentication, String>
