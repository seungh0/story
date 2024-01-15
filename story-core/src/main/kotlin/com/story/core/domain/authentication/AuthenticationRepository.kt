package com.story.core.domain.authentication

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AuthenticationRepository : CoroutineCrudRepository<Authentication, String>
