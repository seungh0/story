package com.story.datacenter.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.reactive.server.WebTestClient

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
internal abstract class ApiTestBase {

    @Autowired
    protected lateinit var webClient: WebTestClient

}
