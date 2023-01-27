package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class SubscriptionUnSubscriberTest(
    private val subscriptionUnSubscriber: SubscriptionUnSubscriber,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
) : IntegrationTest() {

    @AfterEach
    override fun cleanUp() {
        runBlocking {
            subscriptionCoroutineRepository.deleteAll()
            subscriptionReverseCoroutineRepository.deleteAll()
            subscriptionCounterCoroutineRepository.deleteAll()
        }
    }

    @Test
    fun `구독 정보를 취소합니다`(): Unit = runBlocking {
        // given
        val serviceType = ServiceType.TWEETER
        val subscriptionType = "follow"
        val targetId = "10000"
        val subscriberId = "2000"

        subscriptionCoroutineRepository.save(
            SubscriptionFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
                slotNo = 1L,
            )
        )

        subscriptionReverseCoroutineRepository.save(
            SubscriptionReverseFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
                slotNo = 1L,
            )
        )

        subscriptionCounterCoroutineRepository.increase(
            key = SubscriptionCounterPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
            )
        )

        // when
        subscriptionUnSubscriber.unsubscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()
        assertThat(subscriptions).isEmpty()

        val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
        assertThat(subscriptionReverses).isEmpty()

        val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
        assertThat(subscriptionCounters).hasSize(1)
        subscriptionCounters[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.count).isEqualTo(0L)
        }
    }

    @Test
    fun `구독 정보가 없을 때 구독 정보를 취소하더라도 멱등성을 보장한다`(): Unit = runBlocking {
        // given
        val serviceType = ServiceType.TWEETER
        val subscriptionType = "follow"
        val targetId = "10000"
        val subscriberId = "2000"

        // when
        subscriptionUnSubscriber.unsubscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()
        assertThat(subscriptions).isEmpty()

        val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
        assertThat(subscriptionReverses).isEmpty()

        val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
        assertThat(subscriptionCounters).isEmpty()
    }

}
