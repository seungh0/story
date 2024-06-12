package com.story.core.support.circuitbreaker.resilience4j

import com.story.core.support.circuitbreaker.CircuitBreakerType
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
internal class Resilience4jCircuitBreakerConfig {

    @Bean
    fun redisCacheCircuitBreaker() = settings(
        circuitBreakerType = CircuitBreakerType.REDIS_CACHE,
        minimumNumberOfCalls = 10,
        slowCallDurationThreshold = Duration.ofSeconds(5),
        recordExceptions = listOf(Exception::class.java),
    )

    private fun settings(
        circuitBreakerType: CircuitBreakerType,
        slidingWindowType: CircuitBreakerConfig.SlidingWindowType = CircuitBreakerConfig.SlidingWindowType.TIME_BASED,
        slidingWindowSize: Int = 60, // N초 동안의 호출 정보를 집계해서 계산
        failureRateThreshold: Float = 50f, // N% 실패 임계치 (서킷 OPEN)
        minimumNumberOfCalls: Int, // 최소 N번 이상의 요청부터 서킷 에러률이 계산된다
        slowCallDurationThreshold: Duration = Duration.ofSeconds(5), // Slow Call 기준 (N초)
        slowCallRateThreshold: Float = 80f, // Slow Call 임계값 (%)
        waitDurationInOpenState: Duration = Duration.ofSeconds(5), // 서킷이 OPEN -> Half Open 까지의 시간
        permittedNumberOfCallsInHalfOpenState: Int = 10, // 서킷이 Half Open인 경우 허용할 요청 횟수
        automaticTransitionFromOpenToHalfOpenEnabled: Boolean = true, // 호출이 없어도 모니터링 스레드에 의해 자동으로 서킷이 열리는지 여부
        recordExceptions: List<Class<out Throwable>>,
    ): Customizer<ReactiveResilience4JCircuitBreakerFactory> = Customizer { factory ->
        factory.configure(
            { builder ->
                builder.circuitBreakerConfig(
                    CircuitBreakerConfig.Builder()
                        .slidingWindowType(slidingWindowType)
                        .slidingWindowSize(slidingWindowSize)
                        .failureRateThreshold(failureRateThreshold)
                        .minimumNumberOfCalls(minimumNumberOfCalls)
                        .slowCallDurationThreshold(slowCallDurationThreshold)
                        .slowCallRateThreshold(slowCallRateThreshold)
                        .waitDurationInOpenState(waitDurationInOpenState)
                        .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                        .automaticTransitionFromOpenToHalfOpenEnabled(automaticTransitionFromOpenToHalfOpenEnabled)
                        .recordExceptions(*recordExceptions.toTypedArray())
                        .build(),
                )
            },
            circuitBreakerType.name,
        )
    }

}
