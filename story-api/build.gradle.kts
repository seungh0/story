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

    // Spring Rest Docs
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor:3.0.0")
}

application {
    mainClass.set("com.story.platform.api.ApiApplicationKt")
}
