package com.story.platform.core.domain.authentication

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AuthenticationKeyRepository : CoroutineCrudRepository<AuthenticationKey, AuthenticationKeyPrimaryKey>
