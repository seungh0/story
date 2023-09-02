package com.story.platform.core.domain.reaction

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.spring.EventProducer
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import kotlinx.coroutines.CoroutineDispatcher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class ReactionEventProducer(
    @Qualifier(KafkaProducerConfig.REACTION_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
)
