import kotlin.time.Duration
import kotlin.time.DurationUnit

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.3.12"
    application
}

application {
    mainClass.set("com.example.server.ApplicationKt")
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
    implementation(project(":commons-api-models"))
    implementation(project(":user-api-models"))
    implementation(project(":order-api-models"))
    implementation(project(":health-api-models"))
    implementation(project(":application-core"))
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // Ktor Server - Use BOM for consistent versions
    implementation(platform("io.ktor:ktor-bom:2.3.12"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-request-validation")

    // Metrics & Health
    implementation("io.ktor:ktor-server-metrics-micrometer")
    implementation("io.micrometer:micrometer-core:1.12.5")
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.5")

    // OpenAPI/Swagger - Use compatible version
    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Dependency Injection - Replacing Dagger with manual DI for now
    // implementation("com.google.dagger:dagger:2.52")
    // kapt("com.google.dagger:dagger-compiler:2.52")
    implementation("javax.inject:javax.inject:1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Testing
    testImplementation(platform("io.ktor:ktor-bom:2.3.12"))
    testImplementation("io.ktor:ktor-server-tests")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation(project(":test-fixtures"))
}

tasks.test {
    useJUnitPlatform()
}
