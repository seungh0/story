package com.story.core.infrastructure.spring

import com.story.core.common.annotation.CpuBound
import com.story.core.common.annotation.IOBound
import com.story.core.common.coroutine.CoroutineConfig
import com.story.core.common.error.ErrorCode
import com.story.core.common.error.InternalServerException
import com.story.core.common.model.dto.ApiResponse
import com.story.core.common.warmer.Warmer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.boot.availability.ApplicationAvailability
import org.springframework.boot.availability.LivenessState
import org.springframework.boot.availability.ReadinessState
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class AvailabilityChecker(
    private val applicationAvailability: ApplicationAvailability,

    @IOBound
    private val ioBoundCoroutineDispatcher: CoroutineDispatcher,

    @CpuBound
    private val cpuBoundCoroutineDispatcher: CoroutineDispatcher,

    private val warmer: Warmer,
) {

    suspend fun livenessCheck(): ResponseEntity<ApiResponse<Nothing?>> {
        val isIoBoundLivenessCorrect = withContext(ioBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.livenessState == LivenessState.CORRECT
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(
                        message = "Liveness Probe 체크 중 Coroutine Timeout이 발생하였습니다",
                        cause = exception
                    )
                }
            }
        }

        if (!isIoBoundLivenessCorrect) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.fail(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        val isCpuBoundLivenessCorrect = withContext(cpuBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.livenessState == LivenessState.CORRECT
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(
                        message = "Liveness Probe 체크 중 Coroutine Timeout이 발생하였습니다",
                        cause = exception
                    )
                }
            }
        }

        if (!isCpuBoundLivenessCorrect) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.fail(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        return ResponseEntity.ok(ApiResponse.OK)
    }

    suspend fun readinessCheck(): ResponseEntity<ApiResponse<Nothing?>> {
        val isIoBoundAcceptingTraffic = withContext(ioBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.readinessState == ReadinessState.ACCEPTING_TRAFFIC
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(
                        message = "Readiness Probe 체크 중 Coroutine Timeout이 발생하였습니다",
                        cause = exception
                    )
                }
            }
        }

        if (!isIoBoundAcceptingTraffic) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.fail(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        val isCpuBoundAcceptingTraffic = withContext(cpuBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.readinessState == ReadinessState.ACCEPTING_TRAFFIC
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException(
                        message = "Readiness Probe 체크 중 Coroutine Timeout이 발생하였습니다",
                        cause = exception
                    )
                }
            }
        }

        if (!isCpuBoundAcceptingTraffic) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.fail(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        warmer.run()

        return ResponseEntity.ok(ApiResponse.OK)
    }

}
