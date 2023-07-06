val asciidoctorExtensions: Configuration by configurations.creating

tasks.test {
    outputs.dir("build/generated-snippets")
}

tasks.asciidoctor {
    inputs.dir("build/generated-snippets")
    dependsOn(tasks.docsTest)
    configurations(asciidoctorExtensions.name)
    baseDirFollowsSourceFile()
}

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

application {
    mainClass.set("com.story.platform.api.ApiApplicationKt")
}
