package com.story.platform.core.domain.subscription

import com.story.platform.core.IntegrationTest
import com.story.platform.core.common.enums.ServiceType
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class SubscriptionSubscriberTest(
    private val subscriptionSubscriber: SubscriptionSubscriber,
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
    fun `구독 정보를 추가한다`(): Unit = runBlocking {
        // given
        val serviceType = ServiceType.TWEETER
        val subscriptionType = "follow"
        val targetId = "10000"
        val subscriberId = "2000"

        // when
        subscriptionSubscriber.subscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()

        assertThat(subscriptions).hasSize(1)
        subscriptions[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.subscriberId).isEqualTo(subscriberId)
            assertThat(it.key.slotId).isEqualTo(1L)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.extraJson).isNull()
        }

        val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
        assertThat(subscriptionReverses).hasSize(1)
        subscriptionReverses[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.subscriberId).isEqualTo(subscriberId)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.slotId).isEqualTo(1L)
        }

        val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
        assertThat(subscriptionCounters).hasSize(1)
        subscriptionCounters[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.count).isEqualTo(1L)
        }
    }

    @Test
    fun `구독 정보를 추가할때 이미 구독 정보가 있는 경우라도 멱등성을 보장한다`(): Unit = runBlocking {
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
                slotId = 1L,
            )
        )

        subscriptionReverseCoroutineRepository.save(
            SubscriptionReverseFixture.create(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
                slotId = 1L,
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
        subscriptionSubscriber.subscribe(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            subscriberId = subscriberId,
        )

        // then
        val subscriptions: List<Subscription> = subscriptionCoroutineRepository.findAll().toList()

        assertThat(subscriptions).hasSize(1)
        subscriptions[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.subscriberId).isEqualTo(subscriberId)
            assertThat(it.key.slotId).isEqualTo(1L)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.extraJson).isNull()
        }

        val subscriptionReverses = subscriptionReverseCoroutineRepository.findAll().toList()
        assertThat(subscriptionReverses).hasSize(1)
        subscriptionReverses[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.subscriberId).isEqualTo(subscriberId)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.slotId).isEqualTo(1L)
        }

        val subscriptionCounters = subscriptionCounterCoroutineRepository.findAll().toList()
        assertThat(subscriptionCounters).hasSize(1)
        subscriptionCounters[0].also {
            assertThat(it.key.serviceType).isEqualTo(serviceType)
            assertThat(it.key.subscriptionType).isEqualTo(subscriptionType)
            assertThat(it.key.targetId).isEqualTo(targetId)
            assertThat(it.count).isEqualTo(1L)
        }
    }

}
