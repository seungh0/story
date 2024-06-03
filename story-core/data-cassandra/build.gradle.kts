dependencies {
    // Core
    implementation(project(":story-core:domain"))

    // Cassandra
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Test
    testRuntimeOnly(project(":story-core:data-redis"))

    // Test Fixtures
    testFixturesImplementation(testFixtures(project(":story-core:domain")))
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
