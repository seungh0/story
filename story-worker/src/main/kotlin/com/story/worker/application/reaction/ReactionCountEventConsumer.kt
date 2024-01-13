package com.story.worker.application.reaction

import com.story.core.common.annotation.EventConsumer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.domain.event.EventRecord
import com.story.core.domain.reaction.ReactionCountPrimaryKey
import com.story.core.domain.reaction.ReactionCountRepository
import com.story.core.domain.reaction.ReactionEvent
import com.story.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class ReactionCountEventConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val reactionCountRepository: ReactionCountRepository,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.reaction.name}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.DEFAULT_BATCH_KAFKA_CONSUMER,
    )
    fun handleReactionCount(@Payload records: List<ConsumerRecord<String, String>>) = runBlocking {
        val reactions = records.map { record ->
            val event = record.value().toObject(EventRecord::class.java)
                ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

            event.payload.toJson().toObject(ReactionEvent::class.java)
                ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")
        }

        val reactionCountMap = reactions.flatMap { reaction ->
            (reaction.createdOptionIds.map { emotionId -> emotionId to 1L } + reaction.deletedOptionIds.map { emotionId -> emotionId to -1L })
                .map { (emotionId, delta) ->
                    ReactionCountPrimaryKey(
                        workspaceId = reaction.workspaceId,
                        componentId = reaction.componentId,
                        spaceId = reaction.spaceId,
                        emotionId = emotionId
                    ) to delta
                }
        }
            .groupingBy { it.first }
            .aggregate { _, accumulator: Long?, element, _ -> (accumulator ?: 0L) + element.second }
            .filter { it.value != 0L }

        if (reactionCountMap.isEmpty()) {
            return@runBlocking
        }

        withContext(dispatcher) {
            reactionCountMap.map { (key, count) -> key to count }
                .chunked(PARALLEL_COUNT)
                .forEach { chunkedKeyCounts ->
                    chunkedKeyCounts.map { (key, count) ->
                        launch {
                            reactionCountRepository.increase(key = key, count = count)
                        }
                    }.joinAll()
                }
        }
    }

    companion object {
        private const val GROUP_ID = "reaction-count-event-consumer"
        private const val PARALLEL_COUNT = 10
    }

}
