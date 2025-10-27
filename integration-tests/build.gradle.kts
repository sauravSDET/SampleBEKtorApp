import java.time.Duration

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

// Java 21 configuration
tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    // Project modules
    testImplementation(project(":ktor-server"))
    testImplementation(project(":test-fixtures"))

    // Add API model dependencies
    testImplementation(project(":user-api-models"))
    testImplementation(project(":order-api-models"))
    testImplementation(project(":commons-api-models"))

    // Ktor testing
    testImplementation("io.ktor:ktor-server-tests:2.3.12")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    testImplementation("io.ktor:ktor-client-cio:2.3.12")
    testImplementation("io.ktor:ktor-client-logging:2.3.12")

    // Testing frameworks
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("org.testcontainers:testcontainers:1.20.3")
    testImplementation("org.testcontainers:postgresql:1.20.3")
    testImplementation("org.testcontainers:kafka:1.20.3")
    testImplementation("org.testcontainers:junit-jupiter:1.20.3")

    // Serialization
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Assertions
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")

    // Spring test support for transaction management
    testImplementation("org.springframework:spring-test:6.1.6")
    testImplementation("org.springframework:spring-tx:6.1.6")

    // Contract testing
    testImplementation("io.swagger.parser.v3:swagger-parser:2.1.19")
    testImplementation("com.atlassian.oai:swagger-request-validator-core:2.40.0")
}

tasks.test {
    useJUnitPlatform {
        includeTags("integration")
    }

    systemProperty("testcontainers.reuse.enable", "true")
    systemProperty("testcontainers.ryuk.disabled", "true")

    // Longer timeout for integration tests
    timeout.set(Duration.ofMinutes(15))
}
