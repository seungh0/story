dependencies {
    implementation(project(":story-core"))

    // Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
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
