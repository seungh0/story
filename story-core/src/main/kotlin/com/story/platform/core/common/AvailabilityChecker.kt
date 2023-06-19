package com.story.platform.core.common

import com.story.platform.core.common.error.ErrorCode
import com.story.platform.core.common.error.InternalServerException
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.support.coroutine.CoroutineConfig
import com.story.platform.core.support.coroutine.CpuBound
import com.story.platform.core.support.coroutine.IOBound
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
) {

    suspend fun livenessCheck(): ResponseEntity<ApiResponse<String>> {
        val isIoBoundLivenessCorrect = withContext(ioBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.livenessState != LivenessState.BROKEN
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException("Liveness Probe 체크 중 Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }

        if (!isIoBoundLivenessCorrect) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        val isCpuBoundLivenessCorrect = withContext(cpuBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.livenessState != LivenessState.BROKEN
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException("Liveness Probe 체크 중 Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }

        if (!isCpuBoundLivenessCorrect) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        return ResponseEntity.ok(ApiResponse.OK)
    }

    suspend fun readinessCheck(): ResponseEntity<ApiResponse<String>> {
        val isIoBoundAcceptingTraffic = withContext(ioBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.readinessState != ReadinessState.REFUSING_TRAFFIC
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException("Readiness Probe 체크 중 Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }

        if (!isIoBoundAcceptingTraffic) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        val isCpuBoundAcceptingTraffic = withContext(cpuBoundCoroutineDispatcher) {
            return@withContext withTimeout(timeMillis = CoroutineConfig.DEFAULT_TIMEOUT_MS) {
                try {
                    return@withTimeout applicationAvailability.readinessState != ReadinessState.REFUSING_TRAFFIC
                } catch (exception: TimeoutCancellationException) {
                    throw InternalServerException("Readiness Probe 체크 중 Coroutine Timeout이 발생하였습니다", exception)
                }
            }
        }

        if (!isCpuBoundAcceptingTraffic) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(ErrorCode.E503_SERVICE_UNAVAILABLE))
        }

        return ResponseEntity.ok(ApiResponse.OK)
    }

}
