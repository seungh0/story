import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinLoggingJvmVersion = "2.1.20"
val kotlinxCoroutinesTestVersion = "1.6.4"
val embeddedCassandraVersion = "4.0.7"
val springMockkVersion = "3.1.1"

plugins {
    id("org.springframework.boot") version "2.7.7"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("application")
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

java.sourceCompatibility = JavaVersion.VERSION_11

allprojects {
    group = "com.story.platform"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "application")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "org.asciidoctor.jvm.convert")

    dependencies {
        // Spring
        implementation("org.springframework.boot:spring-boot-starter-validation")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")

        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Kotlin Logging
        implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingJvmVersion")

        // Coroutines
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesTestVersion")

        // Jackson
        implementation("org.springframework.boot:spring-boot-starter-json")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        // Reactor
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        testImplementation("io.projectreactor:reactor-test")

        // Embedded Cassandra
        implementation("com.github.nosan:embedded-cassandra:$embeddedCassandraVersion")

        // Spring MockK
        testImplementation("com.ninja-squad:springmockk:${springMockkVersion}")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            exceptionFormat = FULL
            showCauses = true
            showStackTraces = true
            events = setOf(FAILED)
        }
    }

    task<Test>("unitTest") {
        description = "Unit Test"
        group = "verification"
        useJUnitPlatform {
            excludeTags("integration-test")
            excludeTags("docs-test")
        }
    }

    task<Test>("integrationTest") {
        description = "Integration Test"
        group = "verification"
        useJUnitPlatform {
            includeTags("integration-test")
        }
    }

    task<Test>("docsTest") {
        description = "Docs Test"
        group = "verification"
        useJUnitPlatform {
            includeTags("docs-test")
        }
    }
}

application {
    mainClass.set("com.story.platform.api.ApiApplicationKt")
}
