val redissonVersion: String by project.extra

dependencies {
    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Cassandra
    api("org.springframework.boot:spring-boot-starter-data-cassandra-reactive") // TODO: 모듈 정리 후 제거

    // Kafka
    api("org.springframework.kafka:spring-kafka")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    // CircuitBreaker
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    // CommonsLang3
    api("org.apache.commons:commons-lang3")

    // Jackson
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Test
    testImplementation(project(":story-core:data-cassandra"))
    testImplementation(project(":story-core:data-redis"))

    // Test Fixtures
    testImplementation(testFixtures(project(":story-core:domain")))
    testImplementation(testFixtures(project(":story-core:data-cassandra")))

    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
