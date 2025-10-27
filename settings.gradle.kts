rootProject.name = "SampleBackendKtorApp"

// Core modules
include(":ktor-server")
include(":ktor-client")

// API Models modules (similar to the working project structure)
include(":commons-api-models")
include(":user-api-models")
include(":order-api-models")
include(":health-api-models")

// Application modules
include(":application-core")
include(":domain")
include(":infrastructure")

// Test modules
include(":test-fixtures")
include(":integration-tests")
include(":performance-tests") // Added the new performance-tests module
