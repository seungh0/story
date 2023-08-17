package com.story.platform.publisher.domain

import com.story.platform.core.common.model.dto.ApiResponse
import com.story.platform.core.common.spring.AvailabilityChecker
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AvailabilityCheckApi(
    private val availabilityChecker: AvailabilityChecker,
) {

    @GetMapping("/health/liveness")
    suspend fun livenessCheck(): ResponseEntity<ApiResponse<Nothing?>> = availabilityChecker.livenessCheck()

    @GetMapping("/health/readiness")
    suspend fun readinessCheck(): ResponseEntity<ApiResponse<Nothing?>> = availabilityChecker.readinessCheck()

}
