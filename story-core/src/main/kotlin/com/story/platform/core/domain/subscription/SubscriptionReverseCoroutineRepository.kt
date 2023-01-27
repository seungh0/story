package com.story.platform.core.domain.subscription

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionReverseCoroutineRepository :
    CoroutineCrudRepository<SubscriptionReverse, SubscriptionReversePrimaryKey>