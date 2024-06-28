dependencies {
    // Core
    implementation(project(":story-core:domain"))
    runtimeOnly(project(":story-core:data-cassandra"))
    runtimeOnly(project(":story-core:data-redis"))

    // Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Actuator
    runtimeOnly("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer
    implementation("io.micrometer:micrometer-observation")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.micrometer:micrometer-tracing")

    // Test Fixtures
    testImplementation(testFixtures(project(":story-core:data-cassandra")))
    testImplementation(testFixtures(project(":story-core:domain")))
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}

application {
    mainClass.set("com.story.distributor.StoreDistributorApplicationKt")
}
