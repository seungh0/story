dependencies {
    implementation(project(":story-core"))

    // Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer Prometheus
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}

application {
    mainClass.set("com.story.platform.apiconsumer.ApiConsumerApplicationKt")
}
