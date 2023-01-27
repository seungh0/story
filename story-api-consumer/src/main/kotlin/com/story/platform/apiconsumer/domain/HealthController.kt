package com.story.platform.apiconsumer.domain

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health() = com.story.platform.core.common.model.ApiResponse.OK

}
