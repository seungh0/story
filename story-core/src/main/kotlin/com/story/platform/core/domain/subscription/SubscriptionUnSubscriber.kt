package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopicFinder
import com.story.platform.core.infrastructure.kafka.TopicType
import com.story.platform.core.support.json.toJson
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
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriberCoroutineRepository: SubscriberCoroutineRepository,
    private val subscriberCounterCoroutineRepository: SubscriberCounterCoroutineRepository,
    private val subscriberDistributedCoroutineRepository: SubscriberDistributedCoroutineRepository,
    @Qualifier(KafkaProducerConfig.SUBSCRIPTION_KAFKA_TEMPLATE)
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun unsubscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val subscriptionReverse = subscriptionCoroutineRepository.findById(
            SubscriptionPrimaryKey(
                serviceType = serviceType,
                subscriptionType = subscriptionType,
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        if (subscriptionReverse == null || subscriptionReverse.isDeleted()) {
            return
        }

        val jobs = mutableListOf<Job>()
        withContext(Dispatchers.IO) {
            jobs += launch {
                val subscription = subscriberCoroutineRepository.findById(
                    SubscriberPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = subscriptionReverse.slotId,
                        subscriberId = subscriberId,
                    )
                )
                subscriptionReverse.delete()

                val subscriptionDistributed = subscriberDistributedCoroutineRepository.findById(
                    SubscriberDistributedPrimaryKey.of(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        subscriberId = subscriberId,
                    )
                )

                reactiveCassandraOperations.batchOps()
                    .delete(subscription)
                    .insert(subscriptionReverse)
                    .delete(subscriptionDistributed)
                    .execute()
                    .awaitSingleOrNull()
            }

            jobs += launch {
                subscriberCounterCoroutineRepository.decrease(
                    SubscriberCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                    )
                )
            }
            jobs.joinAll()
        }

        val event = SubscriptionEvent.deleted(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), subscriberId, event.toJson())
    }

}
