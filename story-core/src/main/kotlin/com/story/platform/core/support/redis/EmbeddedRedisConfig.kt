package com.story.platform.core.support.redis

import com.story.platform.core.common.partition.ProcessUtils
import com.story.platform.core.common.utils.LoggerUtilsExtension.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Profile("test")
@Configuration
class EmbeddedRedisConfig(
    private val redisProperties: RedisProperties,
) {

    private var redisServer: RedisServer? = null

    @Value("\${spring.redis.port}")
    private var embeddedRedisPort = 0

    @PostConstruct
    fun startEmbeddedRedis() {
        var retry = 0
        var waitMillis = 1000

        while (++retry <= 3) {
            try {
                embeddedRedisPort = availablePort
                if (redisServer == null || !redisServer!!.isActive) {
                    redisServer = RedisServer.builder()
                        .port(embeddedRedisPort)
                        .setting("maxmemory 128M")
                        .setting("daemonize no")
                        .setting("appendonly no")
                        .build()
                    redisServer!!.start()
                    log.info("임베디드 레디스 서버가 시작되었습니다. port: {}", embeddedRedisPort)
                    return
                }
                log.info("임베디드 레디스 서버가 실행 중이라 재활용 합니다 port: {}", embeddedRedisPort)
                return
            } catch (throwable: Throwable) {
                log.error("임베디드 레디스를 실행하지 못하였습니다. retry: ($retry)", throwable)
                waitMillis *= 2
            }
            Thread.sleep(waitMillis.toLong())
        }
        throw com.story.platform.core.common.error.InternalServerException("임베디드 레디스를 실행하지 못하였습니다")
    }

    private val availablePort: Int
        get() = try {
            if (ProcessUtils.isRunningPort(embeddedRedisPort)) ProcessUtils.findAvailableRandomPort() else embeddedRedisPort
        } catch (exception: IOException) {
            throw com.story.platform.core.common.error.InternalServerException(
                "임베디드 Redis 서버 시작를 위해 사용 가능한 포트를 찾는 중 에러가 발생하였습니다",
                exception
            )
        }

    @PreDestroy
    fun stopEmbeddedRedis() {
        if (redisServer != null && redisServer!!.isActive) {
            redisServer!!.stop()
            log.info("임베디드 레디스 서버가 종료됩니다")
        }
    }

    @Primary
    @Bean
    fun embeddedRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(redisProperties.host, embeddedRedisPort)
    }

}
