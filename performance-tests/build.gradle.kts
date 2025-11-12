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
    implementation(project(":ktor-server"))
    implementation(project(":test-fixtures"))

    // Add API model dependencies
    testImplementation(project(":user-api-models"))
    testImplementation(project(":order-api-models"))
    testImplementation(project(":commons-api-models"))

    // K6 and performance testing dependencies
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-property:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.ktor:ktor-client-core:2.3.12")
    testImplementation("io.ktor:ktor-client-cio:2.3.12")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    // Performance metrics and monitoring
    testImplementation("io.micrometer:micrometer-core:1.12.5")
    testImplementation("org.testcontainers:testcontainers:1.19.8")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.testcontainers:kafka:1.19.8")

    // Chaos engineering with Toxiproxy
    testImplementation("eu.rekawek.toxiproxy:toxiproxy-java:2.1.7")

    // Async and coroutines for load testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // DateTime handling
    testImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Transaction management for testing - Java 21 compatible versions
    testImplementation("org.springframework:spring-tx:6.1.6")
    testImplementation("org.springframework:spring-test:6.1.6")
    testImplementation("com.h2database:h2:2.2.224") // In-memory database for transaction testing
}

tasks.test {
    useJUnitPlatform {
        includeTags("performance", "chaos", "e2e")
    }
    systemProperty("performance.test.enabled", "false")
    systemProperty("testcontainers.reuse.enable", "true")
    maxHeapSize = "2g"

    // Enhanced configuration for transaction testing
    systemProperty("test.database.transactions.enabled", "true")
    systemProperty("test.isolation.level", "READ_COMMITTED")
}
