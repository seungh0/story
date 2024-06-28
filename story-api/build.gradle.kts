val asciidoctorExtensions: Configuration by configurations.creating

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

    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor")

    // Test Fixtures
    testImplementation(testFixtures(project(":story-core:domain")))
    testImplementation(testFixtures(project(":story-core:data-cassandra")))
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}

tasks.docsTest {
    outputs.dir("build/generated-snippets")
}

tasks.asciidoctor {
    inputs.dir("build/generated-snippets")
    dependsOn(tasks.docsTest)
    configurations(asciidoctorExtensions.name)
    baseDirFollowsSourceFile()
}

application {
    mainClass.set("com.story.api.ApiApplicationKt")
}
