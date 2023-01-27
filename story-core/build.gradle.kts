tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

val commonsLang3Version = "3.12.0"
val caffeineCacheVersion = "2.9.3"

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

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    testImplementation(testFixtures(project(":story-core")))
}
