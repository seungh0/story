package com.story.platform.worker.event.config

import com.story.platform.worker.event.IntegrationTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldNotBe
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.test.web.reactive.server.WebTestClient

@IntegrationTest
class SpringActuatorConfigTest(
    private val environment: Environment,
    private val webTestClient: WebTestClient,
) : FunSpec({

    test("서비스 포트와 Actuator 포트는 분리된다") {
        // when & then
        environment["management.server.port"] shouldNotBe environment.getProperty("server.port")
    }

    test("Actuator Endpoint로 접근시 인증이 필요하다") {
        webTestClient.get()
            .uri("/monitoring")
            .exchange()
            .expectStatus().isUnauthorized
    }

    test("불필요한 Actuator가 외부로 노출되지 않아야 한다") {
        // when & then
        val exposures = environment.getProperty("management.endpoints.web.exposure.include")?.split(",") ?: emptyList()

        exposures shouldHaveSize 2
        exposures shouldContainExactlyInAnyOrder listOf("health", "prometheus")
    }

})
