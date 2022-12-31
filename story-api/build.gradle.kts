dependencies {
    implementation(project(":story-core"))

    // Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

application {
    mainClass.set("com.story.pushcenter.api.ApiApplicationKt")
}
