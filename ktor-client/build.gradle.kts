plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Project modules
    implementation(project(":commons-api-models"))
    implementation(project(":user-api-models"))
    implementation(project(":order-api-models"))
    implementation(project(":health-api-models"))

    // Ktor Client - Use BOM for consistent versions
    implementation(platform("io.ktor:ktor-bom:2.3.12"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-logging")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Testing
    testImplementation("io.ktor:ktor-client-mock")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testImplementation(project(":test-fixtures"))
}

tasks.test {
    useJUnitPlatform()
}
