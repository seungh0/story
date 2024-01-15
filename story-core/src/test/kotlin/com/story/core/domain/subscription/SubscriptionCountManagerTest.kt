package com.story.core.domain.subscription

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow

class SubscriptionCountManagerTest : FunSpec({

    val subscriberCountMap = mutableMapOf<SubscriberCountPrimaryKey, Long>()
    val subscriptionCountMap = mutableMapOf<SubscriptionCountPrimaryKey, Long>()

    val subscriptionCountManager = SubscriptionCountManager(
        subscriberCountRepository = object : SubscriberCountRepository {
            override suspend fun increase(key: SubscriberCountPrimaryKey, count: Long) {
                subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) + count
            }

            override suspend fun decrease(key: SubscriberCountPrimaryKey, count: Long) {
                subscriberCountMap[key] = (subscriberCountMap[key] ?: 0L) - count
            }

            override suspend fun count(): Long {
                TODO("Not yet implemented")
            }

            override suspend fun delete(entity: SubscriberCount) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAll() {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAll(entities: Iterable<SubscriberCount>) {
                TODO("Not yet implemented")
            }

            override suspend fun <S : SubscriberCount> deleteAll(entityStream: Flow<S>) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAllById(ids: Iterable<SubscriberCountPrimaryKey>) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteById(id: SubscriberCountPrimaryKey) {
                TODO("Not yet implemented")
            }

            override suspend fun existsById(id: SubscriberCountPrimaryKey): Boolean {
                TODO("Not yet implemented")
            }

            override fun findAll(): Flow<SubscriberCount> {
                TODO("Not yet implemented")
            }

            override fun findAllById(ids: Iterable<SubscriberCountPrimaryKey>): Flow<SubscriberCount> {
                TODO("Not yet implemented")
            }

            override fun findAllById(ids: Flow<SubscriberCountPrimaryKey>): Flow<SubscriberCount> {
                TODO("Not yet implemented")
            }

            override suspend fun findById(id: SubscriberCountPrimaryKey): SubscriberCount? {
                TODO("Not yet implemented")
            }

            override suspend fun <S : SubscriberCount> save(entity: S): SubscriberCount {
                TODO("Not yet implemented")
            }

            override fun <S : SubscriberCount> saveAll(entities: Iterable<S>): Flow<S> {
                TODO("Not yet implemented")
            }

            override fun <S : SubscriberCount> saveAll(entityStream: Flow<S>): Flow<S> {
                TODO("Not yet implemented")
            }
        },
        subscriptionCountRepository = object : SubscriptionCountRepository {
            override suspend fun increase(key: SubscriptionCountPrimaryKey, count: Long) {
                subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0L) + count
            }

            override suspend fun decrease(key: SubscriptionCountPrimaryKey, count: Long) {
                subscriptionCountMap[key] = (subscriptionCountMap[key] ?: 0L) - count
            }

            override suspend fun count(): Long {
                TODO("Not yet implemented")
            }

            override suspend fun delete(entity: SubscriptionCount) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAll() {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAll(entities: Iterable<SubscriptionCount>) {
                TODO("Not yet implemented")
            }

            override suspend fun <S : SubscriptionCount> deleteAll(entityStream: Flow<S>) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteAllById(ids: Iterable<SubscriptionCountPrimaryKey>) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteById(id: SubscriptionCountPrimaryKey) {
                TODO("Not yet implemented")
            }

            override suspend fun existsById(id: SubscriptionCountPrimaryKey): Boolean {
                TODO("Not yet implemented")
            }

            override fun findAll(): Flow<SubscriptionCount> {
                TODO("Not yet implemented")
            }

            override fun findAllById(ids: Iterable<SubscriptionCountPrimaryKey>): Flow<SubscriptionCount> {
                TODO("Not yet implemented")
            }

            override fun findAllById(ids: Flow<SubscriptionCountPrimaryKey>): Flow<SubscriptionCount> {
                TODO("Not yet implemented")
            }

            override suspend fun findById(id: SubscriptionCountPrimaryKey): SubscriptionCount? {
                TODO("Not yet implemented")
            }

            override suspend fun <S : SubscriptionCount> save(entity: S): SubscriptionCount {
                TODO("Not yet implemented")
            }

            override fun <S : SubscriptionCount> saveAll(entities: Iterable<S>): Flow<S> {
                TODO("Not yet implemented")
            }

            override fun <S : SubscriptionCount> saveAll(entityStream: Flow<S>): Flow<S> {
                TODO("Not yet implemented")
            }
        },
    )

    afterEach {
        subscriberCountMap.clear()
        subscriptionCountMap.clear()
    }

    test("구독 카운트 증가시 구독자와 구독 대상 카운트가 1 증가한다") {
        // given
        val workspaceId = "workspace-id"
        val componentId = "component-id"
        val targetId = "target-id"
        val subscriberId = "subscriber-id"

        // when
        subscriptionCountManager.increase(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        subscriptionCountMap[
            SubscriptionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
            )
        ] shouldBe 1L

        subscriberCountMap[
            SubscriberCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )
        ] shouldBe 1L
    }

    test("구독 카운트 감소시 구독자와 구독 대상 카운트가 1 감소한다") {
        // given
        val workspaceId = "workspace-id"
        val componentId = "component-id"
        val targetId = "target-id"
        val subscriberId = "subscriber-id"

        // when
        subscriptionCountManager.decrease(
            workspaceId = workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        subscriptionCountMap[
            SubscriptionCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                subscriberId = subscriberId,
            )
        ] shouldBe -1L

        subscriberCountMap[
            SubscriberCountPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
            )
        ] shouldBe -1L
    }

})
