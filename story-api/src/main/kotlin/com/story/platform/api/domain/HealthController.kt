package com.story.platform.api.domain

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.model.ApiResponse
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.availability.ReadinessState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController(
    private val applicationAvailability: ApplicationAvailability,
) {

    @GetMapping("/health/readiness")
    fun readinessCheck(): ResponseEntity<ApiResponse<String>> {
        val state = applicationAvailability.readinessState
        if (state == ReadinessState.REFUSING_TRAFFIC) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }
        return ResponseEntity.ok(ApiResponse.OK)
    }

    @GetMapping("/health/liveness")
    fun livenessCheck(): ResponseEntity<ApiResponse<String>> {
        val state = applicationAvailability.livenessState
        if (state == LivenessState.BROKEN) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }
        return ResponseEntity.ok(ApiResponse.OK)
    }

}
