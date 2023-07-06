tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

val redissonVersion = "3.22.1"

dependencies {
    // CommonsLang3
    api("org.apache.commons:commons-lang3")

    // Cassandra
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Kafka
    api("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    testImplementation(testFixtures(project(":story-core")))
}
