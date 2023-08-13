package com.story.platform.core.infrastructure.kafka

import com.story.platform.core.common.logger.LoggerExtension.log
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.FixedBackOff
import java.time.Duration

@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean(name = [POST_CONTAINER_FACTORY])
    fun postKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [SUBSCRIPTION_CONTAINER_FACTORY])
    fun subscriptionKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [COMPONENT_CONTAINER_FACTORY])
    fun componentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    @Bean(name = [AUTHENTICATION_KEY_CONTAINER_FACTORY])
    fun authenticationKeyKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return concurrentKafkaListenerContainerFactory(
            maxPollRecords = 500, // 리밸런싱시 중복 레코드 컨슈밍 가능
            enableAutoCommit = true, // 중복 레코드 컨슈밍 가능
        )
    }

    fun concurrentKafkaListenerContainerFactory(
        maxPollRecords: Int = 500,
        enableAutoCommit: Boolean = true,
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = DefaultKafkaConsumerFactory(
            defaultKafkaConsumerConfig(
                maxPollRecords = maxPollRecords,
                enableAutoCommit = enableAutoCommit,
            )
        )
        factory.setConcurrency(1)

        factory.setCommonErrorHandler(
            DefaultErrorHandler({ consumerRecord, exception ->
                if (consumerRecord.value() == null) {
                    log.error("[Topic: ${consumerRecord.topic()}] value is null", exception)
                } else {
                    log.error(
                        """
                        [topic: ${consumerRecord.topic()}] consume fail
                        cause: ${exception.message}
                        key: ${consumerRecord.key()}
                        value: ${consumerRecord.value()}
                        headers: ${consumerRecord.headers()}
                        offset: ${consumerRecord.offset()}
                        """.trimIndent(),
                        exception
                    )
                }
            }, FixedBackOff(1000L, 3L))
        )
        return factory
    }

    fun defaultKafkaConsumerConfig(
        maxPollRecords: Int = 500,
        enableAutoCommit: Boolean = true,
        autoCommitInterval: Duration = Duration.ofSeconds(5),
        heartbeatInterval: Duration = Duration.ofSeconds(3),
        sessionTimeout: Duration = Duration.ofSeconds(45),
        maxPollInterval: Duration = Duration.ofMinutes(5),
        requestTimeout: Duration = Duration.ofSeconds(30),
    ): Map<String, Any> {
        val config: MutableMap<String, Any> = HashMap()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java

        // 토픽 자동 생성 여부
        config[ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG] = kafkaProperties.admin.isAutoCreate

        // 커밋된 오프셋이 없을 때나 컨슈머가 브로커에 없는 오프셋을 요청할 때 컨슈머가 어떻게 처리할지 여부
        // earliest -> 유효한 오프싯에 없는 한 컨슈머는 파티션의 맨 앞에서부터 읽기를 시작 (컨슈머는 많은 메시지들을 중복 처리할 수 있지만, 데이터 유실은 최소화 가능)
        // latest -> 컨슈머는 파티션의 끝에서부터 읽기를 시작 (중복 처리는 최소화, 일부 메시지는 누락)
        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"

        // poll을 호출할때마다 리턴되는 최대 레코드 수
        config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = maxPollRecords

        // autocommit 여부
        config[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = enableAutoCommit

        // autocommit 주기 (default: 5s)
        config[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = autoCommitInterval.toMillis().toInt()

        // 그룹 코디네이터에게 heartbeat를 보내는 주기 (default: 3s)
        config[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = heartbeatInterval.toMillis().toInt()

        // 해당 설정을 넘어설때까지 heartbeat를 받지 못하면, 죽은 컨슈머로 간주하고 리밸런싱이 일어난다 (default: 45s)
        config[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = sessionTimeout.toMillis().toInt()

        // 해당 시간만큼 poll이 발생하지 않으면 죽은 컨슈머로 간주하고 리밸런싱이 일어난다 (default: 5m)
        config[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = maxPollInterval.toMillis().toInt()

        // Request Timeout (default: 30s)
        config[ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG] = requestTimeout.toMillis().toInt()
        return config
    }

    companion object {
        const val AUTHENTICATION_KEY_CONTAINER_FACTORY = "authenticationKeyContainerFactory"
        const val COMPONENT_CONTAINER_FACTORY = "componentContainerFactory"
        const val POST_CONTAINER_FACTORY = "postContainerFactory"
        const val SUBSCRIPTION_CONTAINER_FACTORY = "subscriptionContainerFactory"
    }

}
