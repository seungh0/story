package com.story.pushcenter.apiconsumer.common.controller

import com.story.pushcenter.apiconsumer.common.dto.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health() = ApiResponse.OK

}
