val redissonVersion: String by project.extra

dependencies {
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Cassandra
    api("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Kafka
    api("org.springframework.kafka:spring-kafka")

    // Redis
    api("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    // CircuitBreaker
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    // CommonsLang3
    api("org.apache.commons:commons-lang3")

    // Jackson
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Test
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testRuntimeOnly(project(":story-core:data-cassandra"))
    testRuntimeOnly(project(":story-core:data-redis"))
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
