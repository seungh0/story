package com.story.platform.core.domain.subscription

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.utils.JsonUtils
import com.story.platform.core.support.kafka.KafkaTopicFinder
import com.story.platform.core.support.kafka.TopicType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionReverseCoroutineRepository: SubscriptionReverseCoroutineRepository,
    private val subscriptionCoroutineRepository: SubscriptionCoroutineRepository,
    private val subscriptionCounterCoroutineRepository: SubscriptionCounterCoroutineRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    suspend fun unsubscribe(
        serviceType: ServiceType,
        subscriptionType: SubscriptionType,
        targetId: String,
        subscriberId: String,
    ) {
        val subscriptionReverse = subscriptionReverseCoroutineRepository.findById(
            SubscriptionReversePrimaryKey(
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
            jobs.add(launch {
                val subscription = subscriptionCoroutineRepository.findById(
                    SubscriptionPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                        slotId = subscriptionReverse.slotId,
                        subscriberId = subscriberId,
                    )
                )
                subscriptionReverse.delete()
                reactiveCassandraOperations.batchOps()
                    .delete(subscription)
                    .insert(subscriptionReverse)
                    .execute()
                    .awaitSingleOrNull()
            })

            jobs.add(launch {
                subscriptionCounterCoroutineRepository.decrease(
                    SubscriptionCounterPrimaryKey(
                        serviceType = serviceType,
                        subscriptionType = subscriptionType,
                        targetId = targetId,
                    )
                )
            })
            jobs.joinAll()
        }

        val event = SubscriptionEvent.deleted(
            serviceType = serviceType,
            subscriptionType = subscriptionType,
            subscriberId = subscriberId,
            targetId = targetId,
        )
        kafkaTemplate.send(KafkaTopicFinder.getTopicName(TopicType.SUBSCRIPTION), JsonUtils.toJson(event))
    }

}
