val asciidoctorExtensions: Configuration by configurations.creating

dependencies {
    implementation(project(":story-core"))

    // Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

tasks.bootJar {
    enabled = true
}

tasks.jar {
    enabled = true
}

tasks.test {
    outputs.dir("build/generated-snippets")
}

tasks.asciidoctor {
    inputs.dir("build/generated-snippets")
    dependsOn(tasks.docsTest)
    configurations(asciidoctorExtensions.name)
    baseDirFollowsSourceFile()
}

application {
    mainClass.set("com.story.platform.api.ApiApplicationKt")
}
