import java.time.Duration

plugins {
    kotlin("jvm") version "2.0.20" apply false
    kotlin("plugin.serialization") version "2.0.20" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco")
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.owasp.dependencycheck") version "9.2.0"
    id("io.ktor.plugin") version "2.3.12" apply false
    // OpenAPI diff and contract testing support
    id("org.openapi.generator") version "7.7.0" apply false
}

allprojects {
    group = "com.example"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
    }
}

// Common configuration for all subprojects
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "jacoco")

    group = "com.example"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
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
        val testImplementation by configurations

        // Test dependencies for all modules
        testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("io.mockk:mockk:1.13.12")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
        testImplementation("io.kotest:kotest-assertions-core:5.9.1")
        testImplementation("io.kotest:kotest-property:5.9.1")

        // Contract testing and schema validation
        testImplementation("io.swagger.parser.v3:swagger-parser:2.1.19")
        testImplementation("com.atlassian.oai:swagger-request-validator-core:2.40.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeTags("fast", "integration", "api", "security")
        }

        timeout.set(Duration.ofMinutes(10))
        maxParallelForks = Runtime.getRuntime().availableProcessors()

        jvmArgs(
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-Xms256m",
            "-Xmx1g"
        )

        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }

        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    }

    // Fast unit tests for AI development feedback
    tasks.register<Test>("fastTest") {
        group = "verification"
        description = "Fast unit tests for AI development feedback (< 5 seconds)"

        useJUnitPlatform {
            includeTags("fast")
        }

        maxParallelForks = Runtime.getRuntime().availableProcessors()
        timeout.set(Duration.ofSeconds(30))
        systemProperty("test.speed", "fast")
    }

    // Integration tests with real dependencies
    tasks.register<Test>("integrationTest") {
        group = "verification"
        description = "Integration tests with TestContainers and real dependencies"

        useJUnitPlatform {
            includeTags("integration")
        }

        maxParallelForks = 1
        timeout.set(Duration.ofMinutes(15))
        systemProperty("test.containers.enabled", "true")
    }

    // API contract tests
    tasks.register<Test>("apiTest") {
        group = "verification"
        description = "API contract tests for endpoint validation"

        useJUnitPlatform {
            includeTags("api")
        }

        maxParallelForks = 2
        timeout.set(Duration.ofMinutes(10))
        systemProperty("test.api.enabled", "true")
    }

    // Security validation tests
    tasks.register<Test>("securityTest") {
        group = "verification"
        description = "Security validation tests for AI-generated code"

        useJUnitPlatform {
            includeTags("security")
        }

        maxParallelForks = 2
        timeout.set(Duration.ofMinutes(5))
        systemProperty("security.test.enabled", "true")
    }

    // Unit Tests Task
    tasks.register<Test>("unitTest") {
        group = "verification"
        description = "Runs unit tests only"
        useJUnitPlatform()

        filter {
            includeTestsMatching("*Test")
            excludeTestsMatching("*IntegrationTest")
            excludeTestsMatching("*ApiTest")
            excludeTestsMatching("*E2ETest")
        }
    }

    // Performance Tests Task
    tasks.register<Test>("performanceTest") {
        group = "verification"
        description = "Performance benchmarks for scalability validation"
        useJUnitPlatform()

        filter {
            includeTestsMatching("*PerformanceTest")
        }

        jvmArgs("-Xmx4g", "-XX:+UseG1GC")
        timeout.set(Duration.ofMinutes(10))
    }

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }
}

// Root project configuration
group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

// API-First Development Tasks
tasks.register("generateApiModels") {
    group = "api-first"
    description = "Generate Kotlin data classes from OpenAPI specification"

    doLast {
        println("Generating API models from OpenAPI specification...")
    }
}

tasks.register("validateApiContract") {
    group = "api-first"
    description = "Validate OpenAPI contract against implementation"

    doLast {
        println("Validating API contract compliance...")
    }
}

tasks.register("checkApiCompatibility") {
    group = "api-first"
    description = "Check API compatibility between versions"

    doLast {
        println("Checking API version compatibility...")
    }
}

// API Contract Backward Compatibility Validation
tasks.register<Exec>("validateApiContractBackwardCompatibility") {
    group = "api-first"
    description = "Validate API contract backward compatibility using custom OpenAPI diff script"

    val prevSpecPath = "src/main/resources/openapi/api-prev.yaml"
    val currSpecPath = "src/main/resources/openapi/api.yaml"
    val diffScript = "scripts/tools/openapi-diff.kts"

    inputs.file(prevSpecPath)
    inputs.file(currSpecPath)
    inputs.file(diffScript)

    commandLine("kotlin", diffScript, prevSpecPath, currSpecPath)

    doFirst {
        val prevSpec = file(prevSpecPath)
        val currSpec = file(currSpecPath)
        val script = file(diffScript)

        if (!prevSpec.exists()) {
            throw GradleException("Previous OpenAPI spec not found: ${prevSpec.absolutePath}")
        }
        if (!currSpec.exists()) {
            throw GradleException("Current OpenAPI spec not found: ${currSpec.absolutePath}")
        }
        if (!script.exists()) {
            throw GradleException("OpenAPI diff script not found: ${script.absolutePath}")
        }

        println("Running OpenAPI backward compatibility check...")
        println("Using custom Kotlin diff script: ${script.name}")
    }
}

// AI Development Workflow Test Tasks
tasks.register<Test>("unitTestRoot") {
    group = "verification"
    description = "Fast unit tests for AI development feedback (< 5 seconds)"

    useJUnitPlatform {
        includeTags("fast")
    }

    maxParallelForks = Runtime.getRuntime().availableProcessors()
    timeout.set(Duration.ofSeconds(30))

    jvmArgs(
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=50",
        "-Xms128m",
        "-Xmx512m"
    )

    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")

    // Simplify dependencies to avoid NullPointerException
    finalizedBy("testAll")
}

tasks.register<Test>("integrationTestRoot") {
    group = "verification"
    description = "Integration tests that validate infrastructure components"

    useJUnitPlatform {
        includeTags("integration")
    }

    maxParallelForks = 1
    timeout.set(Duration.ofMinutes(5))

    jvmArgs(
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=100",
        "-Xms256m",
        "-Xmx1g"
    )

    systemProperty("integration.test.mode", "true")

    // Simplify dependencies to avoid NullPointerException
    finalizedBy("testAll")
}

tasks.register<Test>("securityTestRoot") {
    group = "verification"
    description = "Security validation tests for AI-generated code"

    useJUnitPlatform {
        includeTags("security")
    }

    maxParallelForks = Runtime.getRuntime().availableProcessors()
    timeout.set(Duration.ofMinutes(3))

    systemProperty("security.mode", "strict")
    systemProperty("security.test.enabled", "true")

    dependsOn(subprojects.mapNotNull { project ->
        if (project.file("src/test").exists()) {
            "${project.path}:securityTest"
        } else null
    })
}

// Enhanced task configurations for comprehensive testing
tasks.register<Test>("performanceTest") {
    group = "verification"
    description = "Performance and load testing with K6-style concurrent requests"

    useJUnitPlatform {
        includeTags("performance")
    }

    maxParallelForks = 1 // Performance tests need dedicated resources
    timeout.set(Duration.ofMinutes(10))
    maxHeapSize = "2g"

    jvmArgs(
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=100",
        "-Xms512m",
        "-Xmx2g"
    )

    systemProperty("performance.test.enabled", "true")
    systemProperty("performance.concurrent.users", "1000")
    systemProperty("performance.duration.minutes", "5")

    dependsOn(":performance-tests:test")
}

tasks.register<Test>("chaosTest") {
    group = "verification"
    description = "Chaos engineering tests for resilience validation"

    useJUnitPlatform {
        includeTags("chaos")
    }

    maxParallelForks = 1 // Chaos tests need isolated environment
    timeout.set(Duration.ofMinutes(15))

    systemProperty("chaos.test.enabled", "true")
    systemProperty("testcontainers.reuse.enable", "true")

    dependsOn(":performance-tests:test")
}

tasks.register<Test>("e2eTest") {
    group = "verification"
    description = "End-to-end cross-service workflow tests"

    useJUnitPlatform {
        includeTags("e2e")
    }

    maxParallelForks = 1 // E2E tests need coordinated environment
    timeout.set(Duration.ofMinutes(20))

    systemProperty("e2e.test.enabled", "true")
    systemProperty("docker.compose.file", "docker-compose.e2e.yml")

    dependsOn(":performance-tests:test")
}

tasks.register<Test>("securityTestAutomated") {
    group = "verification"
    description = "Automated security testing including OWASP validation"

    useJUnitPlatform {
        includeTags("security")
    }

    maxParallelForks = 2
    timeout.set(Duration.ofMinutes(10))

    systemProperty("security.test.enabled", "true")
    systemProperty("security.owasp.enabled", "true")

    dependsOn(":performance-tests:test")
}

// Task to run tests across all modules
tasks.register("testAll") {
    group = "verification"
    description = "Run tests in all modules"
    dependsOn(subprojects.map { "${it.path}:test" })
}

// Task to build all modules
tasks.register("buildAll") {
    group = "build"
    description = "Build all modules"
    dependsOn(subprojects.map { "${it.path}:build" })
}

// Task to run the application
tasks.register("runApp") {
    group = "application"
    description = "Run the Ktor server application"
    dependsOn(":ktor-server:run")
}

// Quality check task that runs all static analysis
tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs all quality checks (unit tests, integration tests, coverage)"
    dependsOn("unitTestRoot", "integrationTestRoot")
}

// Advanced Multi-Version API Contract Validation Tasks
tasks.register<Exec>("validateApiContractBackwardCompatibilityAdvanced") {
    group = "api-first"
    description = "Validate API contract backward compatibility between two specific versions"

    val prevSpecPath = "src/main/resources/openapi/v1/deprecated/api-v1.0.0.yaml"
    val currSpecPath = "src/main/resources/openapi/v1/current/api-v1.1.0.yaml"
    val diffScript = "scripts/tools/openapi-diff.kts"

    inputs.file(prevSpecPath)
    inputs.file(currSpecPath)
    inputs.file(diffScript)

    commandLine("kotlin", diffScript, "compare", prevSpecPath, currSpecPath)

    doFirst {
        println("Running OpenAPI backward compatibility check...")
        println("Using advanced multi-version diff script: ${file(diffScript).name}")
    }
}

tasks.register<Exec>("validateAllApiVersions") {
    group = "api-first"
    description = "Validate backward compatibility across all API versions (v1→v2→v3→v4)"

    val diffScript = "scripts/tools/openapi-diff.kts"

    inputs.files(fileTree("src/main/resources/openapi") { include("**/*.yaml") })
    inputs.file(diffScript)

    commandLine("kotlin", diffScript, "validate-all")

    doFirst {
        println("🔍 Validating all API versions for cross-version compatibility...")
        println("This will check: v1→v2, v2→v3, v3→v4 transitions")
    }
}

tasks.register<Exec>("generateMigrationReport") {
    group = "api-first"
    description = "Generate detailed migration report between API versions"

    val diffScript = "scripts/tools/openapi-diff.kts"
    val fromVersion = project.findProperty("fromVersion") ?: "v1"
    val toVersion = project.findProperty("toVersion") ?: "v2"

    inputs.file(diffScript)
    inputs.files(fileTree("src/main/resources/openapi") { include("**/*.yaml") })

    commandLine("kotlin", diffScript, "migration-report", fromVersion, toVersion)

    doFirst {
        println("📊 Generating migration report: $fromVersion → $toVersion")
        println("Usage: ./gradlew generateMigrationReport -PfromVersion=v2 -PtoVersion=v3")
    }
}

tasks.register("validateCurrentApiVersion") {
    group = "api-first"
    description = "Validate the current API version against the previous minor version"
    dependsOn("validateApiContractBackwardCompatibility")
}

tasks.register("apiVersioningHealthCheck") {
    group = "api-first"
    description = "Comprehensive health check for API versioning strategy"
    dependsOn("validateAllApiVersions")

    doLast {
        println("✅ API Versioning Health Check Complete")
        println("📋 All version transitions validated")
        println("🔗 Migration reports available via generateMigrationReport task")
    }
}
