package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.json.toJson
import com.story.platform.core.support.lock.DistributeLock
import com.story.platform.core.support.lock.DistributedLockType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubscriptionSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriberCounterCoroutineRepository: SubscriberCounterCoroutineRepository,
    private val subscriberIdGenerator: SubscriberIdGenerator,
    @Qualifier(KafkaProducerConfig.SUBSCRIPTION_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @DistributeLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'serviceType:' + {#serviceType} + ':subscriptionType:' + {#subscriptionType} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun subscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val primaryKey = SubscriptionPrimaryKey(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        val subscriptionReverse = subscriptionCoroutineRepository.findById(primaryKey)
        if ((subscriptionReverse != null) && subscriptionReverse.isActivated()) {
            saveSubscription(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                targetId = targetId,
                slotId = subscriptionReverse.slotId,
                subscriberId = subscriberId,
                alarm = alarm,
            )
            return
        }

        saveSubscription(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = subscriptionReverse?.slotId ?: SubscriberSlotAssigner.assign(
                subscriberIdGenerator.generate(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    targetId = targetId
                )
            ),
            subscriberId = subscriberId,
            alarm = alarm,
        )

        newSubscriptionPostProcessor(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
    }

    private suspend fun saveSubscription(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        slotId: Long,
        subscriberId: String,
        alarm: Boolean,
    ) {
        val subscriber = Subscriber.of(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            targetId = targetId,
            slotId = slotId,
            subscriberId = subscriberId,
            alarm = alarm,
        )

        reactiveCassandraOperations.batchOps()
            .insert(subscriber)
            .insert(Subscription.of(subscriber = subscriber))
            .insert(SubscriberDistributed.of(subscriber = subscriber))
            .execute()
            .awaitSingleOrNull()
    }

    private suspend fun newSubscriptionPostProcessor(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        subscriberId: String,
        targetId: String,
    ) {
        val jobs = mutableListOf<Job>()
        withContext(Dispatchers.IO) {
            jobs += launch {
                subscriberCounterCoroutineRepository.increase(
                    SubscriberCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                    )
                )
            }

            jobs += launch {
                val event = SubscriptionEvent.created(
                    serviceType = serviceType,
                    subscriptionType = subscriptionType,
                    subscriberId = subscriberId,
                    targetId = targetId,
                )
                kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
            }
        }
        jobs.joinAll()
    }

}
