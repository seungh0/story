dependencies {
    // Core
    implementation(project(":story-core:domain"))

    // Cassandra
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")

    // Test
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testFixturesImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testRuntimeOnly(project(":story-core:data-redis"))

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
