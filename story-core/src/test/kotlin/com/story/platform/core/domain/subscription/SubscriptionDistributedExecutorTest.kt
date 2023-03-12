package com.story.platform.core.domain.subscription

import com.datastax.oss.driver.api.core.cql.BatchType
import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations

@IntegrationTest
class SubscriptionDistributedExecutorTest(
    private val subscriptionDistributedCoroutineRepository: SubscriptionDistributedCoroutineRepository,
    private val subscriptionDistributedExecutor: SubscriptionDistributedExecutor,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) : FunSpec({

    afterEach {
        subscriptionDistributedCoroutineRepository.deleteAll()
    }

    context("특정 대상의 구독자들에게 액션을 실행한다") {
        test("특정 대상의 구독자들을 전체 스캔한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val targetId = "targetId"
            val distributedKey = "001"

            val subscriptions = IntRange(start = 1, endInclusive = 20).map {
                SubscriptionDistributorFixture.create(
                    serviceType = serviceType,
                    subscriptionType = SubscriptionType.FOLLOW,
                    distributedKey = distributedKey,
                    targetId = targetId,
                )
            }

            reactiveCassandraOperations.batchOps(BatchType.UNLOGGED)
                .insert(subscriptions)
                .execute()
                .awaitSingleOrNull()

            var subscribersCount = 0L
            var executeCount = 0L

            // when
            subscriptionDistributedExecutor.executeToTargetSubscribers(
                serviceType = serviceType,
                distributedKey = distributedKey,
                targetId = targetId,
                fetchSize = 3,
            ) {
                subscribersCount += it.size
                executeCount += 1
            }

            // then
            subscribersCount shouldBe 20L
            executeCount shouldBe 7L
        }

        test("다른 대상을 구독한 유저들은 포함되지 않는다") {
            // given
            val serviceType = ServiceType.TWEETER
            val distributedKey = "001"

            val subscription = SubscriptionDistributorFixture.create(
                serviceType = serviceType,
                subscriptionType = SubscriptionType.FOLLOW,
                distributedKey = distributedKey,
                targetId = "another-target-id",
            )
            subscriptionDistributedCoroutineRepository.save(subscription)

            var subscribersCount = 0L
            var executeCount = 0L

            // when
            subscriptionDistributedExecutor.executeToTargetSubscribers(
                serviceType = serviceType,
                distributedKey = distributedKey,
                targetId = "targetId",
                fetchSize = 3,
            ) {
                subscribersCount += it.size
                executeCount += 1
            }

            // then
            subscribersCount shouldBe 0L
            executeCount shouldBe 1L
        }

        test("다른 분산키를 가지고 있는 구독 정보는 포함되지 않는다") {
            // given
            val serviceType = ServiceType.TWEETER
            val targetId = "targetId"
            val distributedKey = "001"

            val subscription = SubscriptionDistributorFixture.create(
                serviceType = serviceType,
                subscriptionType = SubscriptionType.FOLLOW,
                distributedKey = "002",
                targetId = targetId,
            )
            subscriptionDistributedCoroutineRepository.save(subscription)


            var subscribersCount = 0L
            var executeCount = 0L

            // when
            subscriptionDistributedExecutor.executeToTargetSubscribers(
                serviceType = serviceType,
                distributedKey = distributedKey,
                targetId = targetId,
                fetchSize = 3,
            ) {
                subscribersCount += it.size
                executeCount += 1
            }

            // then
            subscribersCount shouldBe 0L
            executeCount shouldBe 1L
        }
    }

})
