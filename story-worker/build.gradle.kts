dependencies {
    // Core
    implementation(project(":story-core"))

    // Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing")

    // Test Fixtures
    testImplementation(testFixtures(project(":story-core")))
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}

application {
    mainClass.set("com.story.worker.StoryWorkerApplicationKt")
}
