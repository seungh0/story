package com.story.core.support.kafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener
import org.springframework.kafka.listener.KafkaConsumerBackoffManager
import org.springframework.kafka.listener.ListenerType
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.adapter.AbstractDelegatingMessageListenerAdapter
import org.springframework.kafka.support.Acknowledgment
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap

class DelayedMessageListenerAdapter<K, V>(
    delegate: MessageListener<K, V>,
    private val kafkaConsumerBackoffManager: KafkaConsumerBackoffManager,
    private val listenerId: String,
) : AbstractDelegatingMessageListenerAdapter<MessageListener<K, V>?>(delegate),
    AcknowledgingConsumerAwareMessageListener<K, V> {

    private val delaysPerTopic: MutableMap<String, Duration> = ConcurrentHashMap()
    private var defaultDelay = DEFAULT_DELAY_VALUE

    init {
        Objects.requireNonNull(kafkaConsumerBackoffManager, "kafkaConsumerBackoffManager cannot be null")
        Objects.requireNonNull(listenerId, "listenerId cannot be null")
    }

    override fun onMessage(
        consumerRecord: ConsumerRecord<K, V>,
        acknowledgment: Acknowledgment?,
        consumer: Consumer<*, *>,
    ) {
        kafkaConsumerBackoffManager.backOffIfNecessary(
            createContext(
                consumerRecord,
                consumerRecord.timestamp() +
                    delaysPerTopic.getOrDefault(consumerRecord.topic(), this.defaultDelay)
                        .toMillis(),
                consumer
            )
        )
        invokeDelegateOnMessage(consumerRecord, acknowledgment, consumer)
    }

    fun setDelayForTopic(topic: String, delay: Duration) {
        Objects.requireNonNull(topic, "Topic cannot be null")
        Objects.requireNonNull(delay, "Delay cannot be null")
        logger.debug { String.format("Setting %s seconds delay for topic %s", delay, topic) }
        delaysPerTopic[topic] = delay
    }

    fun setDefaultDelay(delay: Duration) {
        Objects.requireNonNull(delay, "Delay cannot be null")
        logger.debug {
            String.format(
                "Setting %s seconds delay for listener id %s",
                delay,
                listenerId
            )
        }
        this.defaultDelay = delay
    }

    private fun invokeDelegateOnMessage(
        consumerRecord: ConsumerRecord<K, V>,
        acknowledgment: Acknowledgment?,
        consumer: Consumer<*, *>,
    ) {
        when (this.delegateType!!) {
            ListenerType.ACKNOWLEDGING_CONSUMER_AWARE -> delegate!!.onMessage(consumerRecord, acknowledgment, consumer)

            ListenerType.ACKNOWLEDGING -> delegate!!.onMessage(consumerRecord, acknowledgment)
            ListenerType.CONSUMER_AWARE -> delegate!!.onMessage(consumerRecord, consumer)

            ListenerType.SIMPLE -> delegate!!.onMessage(consumerRecord)
        }
    }

    private fun createContext(
        data: ConsumerRecord<K, V>,
        nextExecutionTimestamp: Long,
        consumer: Consumer<*, *>,
    ): KafkaConsumerBackoffManager.Context {
        return kafkaConsumerBackoffManager.createContext(
            nextExecutionTimestamp,
            this.listenerId, TopicPartition(data.topic(), data.partition()),
            consumer
        )
    }

    override fun onMessage(data: ConsumerRecord<K, V>, consumer: Consumer<*, *>) {
        onMessage(data, null, consumer)
    }

    companion object {
        private val DEFAULT_DELAY_VALUE: Duration = Duration.of(0, ChronoUnit.SECONDS)
    }

}
