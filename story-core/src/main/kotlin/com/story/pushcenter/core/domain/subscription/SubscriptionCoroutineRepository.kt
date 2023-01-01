package com.story.pushcenter.core.domain.subscription

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SubscriptionCoroutineRepository : CoroutineCrudRepository<Subscription, SubscriptionPrimaryKey>
