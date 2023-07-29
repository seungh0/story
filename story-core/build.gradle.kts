val redissonVersion: String by project.extra

dependencies {
    // CommonsLang3
    api("org.apache.commons:commons-lang3")

    // Cassandra
    api("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Kafka
    api("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Redis
    api("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    testImplementation(testFixtures(project(":story-core")))
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
