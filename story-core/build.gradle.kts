tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

val commonsLang3Version = "3.12.0"
val caffeineCacheVersion = "3.1.6"
val redissonVersion = "3.22.0"

dependencies {
    // CommonsLang3
    api("org.apache.commons:commons-lang3:$commonsLang3Version")

    // Cassandra
    api("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Kafka
    api("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineCacheVersion")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    testImplementation(testFixtures(project(":story-core")))
}
