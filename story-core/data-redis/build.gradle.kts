val redissonVersion: String by project.extra

dependencies {
    // Core
    implementation(project(":story-core:domain"))

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.redisson:redisson:$redissonVersion")

    // Test
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testFixturesImplementation(
        testFixtures(project(":story-core:domain")),
    )
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
