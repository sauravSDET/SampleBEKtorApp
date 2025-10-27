plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    // Temporarily disable kapt to resolve Java 21 compatibility issues
    // kotlin("kapt")
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
    implementation(project(":domain"))

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Dependency Injection - Replacing Dagger with manual DI for now
    // implementation("com.google.dagger:dagger:2.52")
    // kapt("com.google.dagger:dagger-compiler:2.52")
    implementation("javax.inject:javax.inject:1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Test fixtures and utilities
    testImplementation(project(":test-fixtures"))

    // Kotest assertions
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")

    // MockK for mocking
    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.test {
    useJUnitPlatform()
}
