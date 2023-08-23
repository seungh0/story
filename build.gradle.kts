import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

val kotlinLoggingJvmVersion: String by project.extra
val springMockkVersion: String by project.extra
val kotestVersion: String by project.extra
val kotestSpringExtensionVersion: String by project.extra

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.3"
    id("application")
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
}

java.sourceCompatibility = JavaVersion.VERSION_17

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
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencies {
        // Spring
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.springframework.boot:spring-boot-starter-aop")

        // Kotlin
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // Kotlin Logging
        implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingJvmVersion")

        // Kotlinx Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

        // Jackson
        implementation("org.springframework.boot:spring-boot-starter-json")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        // Reactor
        testImplementation("io.projectreactor:reactor-test")

        // Spring MockK
        testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

        // Kotest
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestSpringExtensionVersion")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            jvmTarget = "17"
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
        reports {
            html.required.set(false)
            junitXml.required.set(false)
        }
    }

    task<Test>("unitTest") {
        description = "Unit Test"
        group = "verification"
        useJUnitPlatform()
        systemProperty("kotest.tags", "!integration-test")
    }

    task<Test>("integrationTest") {
        description = "Integration Test"
        group = "verification"
        useJUnitPlatform()
        systemProperty("kotest.tags", "integration-test")
    }

    task<Test>("docsTest") {
        description = "Docs Test"
        group = "verification"
        useJUnitPlatform()
        systemProperty("kotest.tags", "docs-test & !integration-test")
    }

    configure<KtlintExtension> {
        version.set("0.45.2")
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

application {
    mainClass.set("com.story.platform.api.ApiApplicationKt")
}
