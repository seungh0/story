val asciidoctorExtensions: Configuration by configurations.creating

val springRestDocsAsciidoctorVersion = "3.0.0"
val micrometerPrometheusVersion = "1.11.1"

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
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerPrometheusVersion")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor:$springRestDocsAsciidoctorVersion")
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
