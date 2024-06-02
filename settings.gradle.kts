plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "story-platform"
include(
    "story-api",
    "story-distributor",
    "story-worker",
    "story-core",
    "story-core:domain",
    "story-core:data-cassandra",
    "story-core:data-redis"
)
