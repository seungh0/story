package com.story.pushcenter.api.domain

import com.story.pushcenter.core.common.model.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health() = ApiResponse.OK

}
