package com.story.platform.core.domain.subscription

import com.datastax.oss.driver.api.core.cql.BatchType
import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.helper.TestCleaner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations

@IntegrationTest
class SubscriptionDistributedExecutorTest(
    private val subscriptionDistributedCoroutineRepository: SubscriptionDistributedCoroutineRepository,
    private val subscriptionDistributedExecutor: SubscriptionDistributedExecutor,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("특정 대상의 구독자들 분산 조회 한다") {
        test("분산 키에 해당하는 구독자들을 대상으로 액션을 수행한다") {
            // given
            val serviceType = ServiceType.TWEETER
            val targetId = "구독 대상자"
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

        test("구독자들에 다른 대상을 구독한 유저들은 포함되지 않는다") {
            // given
            var subscribersCount = 0L
            var executeCount = 0L

            val serviceType = ServiceType.TWEETER
            val distributedKey = "001"

            val subscription = SubscriptionDistributorFixture.create(
                serviceType = serviceType,
                subscriptionType = SubscriptionType.FOLLOW,
                distributedKey = distributedKey,
                targetId = "다른 구독 대상자",
            )
            subscriptionDistributedCoroutineRepository.save(subscription)

            // when
            subscriptionDistributedExecutor.executeToTargetSubscribers(
                serviceType = serviceType,
                distributedKey = distributedKey,
                targetId = "구독 대상자",
                fetchSize = 3,
            ) {
                subscribersCount += it.size
                executeCount += 1
            }

            // then
            subscribersCount shouldBe 0L
            executeCount shouldBe 1L
        }

        test("다른 분산키에 해당하는 구독자들은 포함되지 않는다") {
            // given
            var subscribersCount = 0L
            var executeCount = 0L

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
