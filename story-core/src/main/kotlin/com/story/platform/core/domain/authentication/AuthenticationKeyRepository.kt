package com.story.platform.core.domain.authentication

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationKeyRepository :
    CoroutineCrudRepository<AuthenticationKey, AuthenticationKeyPrimaryKey>
