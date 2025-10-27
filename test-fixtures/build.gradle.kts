plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Core testing frameworks
    api("org.junit.jupiter:junit-jupiter-api:5.11.3")
    api("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    api("io.kotest:kotest-assertions-core:5.9.1")

    // MockK for unit tests (consistent with project)
    api("io.mockk:mockk:1.13.12")

    // TestContainers for integration tests
    api("org.testcontainers:testcontainers:1.20.3")
    api("org.testcontainers:postgresql:1.20.3")
    api("org.testcontainers:kafka:1.20.3")
    api("org.testcontainers:junit-jupiter:1.20.3")
    api("org.testcontainers:r2dbc:1.20.3")

    // Database connection pooling and in-memory database for faster tests
    api("com.zaxxer:HikariCP:5.1.0")
    api("com.h2database:h2:2.2.224")
    api("org.postgresql:postgresql:42.7.4")

    // Ktor testing - Use BOM for consistent versions
    api(platform("io.ktor:ktor-bom:2.3.12"))
    api("io.ktor:ktor-server-tests")
    api("io.ktor:ktor-server-test-host")
    api("io.ktor:ktor-client-content-negotiation")
    api("io.ktor:ktor-client-cio")
    api("io.ktor:ktor-serialization-kotlinx-json")

    // Coroutines testing
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Project dependencies for fixtures
    implementation(project(":domain"))
    implementation(project(":application-core"))
    implementation(project(":commons-api-models"))
    implementation(project(":user-api-models"))
    implementation(project(":order-api-models"))
    implementation(project(":health-api-models"))
}

tasks.test {
    useJUnitPlatform()
}
