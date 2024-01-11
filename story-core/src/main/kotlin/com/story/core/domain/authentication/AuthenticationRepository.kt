package com.story.core.domain.authentication

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticationRepository :
    CoroutineCrudRepository<Authentication, AuthenticationPrimaryKey>
