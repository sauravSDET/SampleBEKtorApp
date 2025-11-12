# Sample Ktor Backend Application - AI/Copilot Ready

A comprehensive enterprise-grade Ktor backend sample project implementing **API-First/Schema-First** approach with **Event-Driven** and **Domain-Driven Design** patterns, optimized for AI/Copilot code generation and large development teams.

## 🤖 AI/Copilot Code Generation Ready

This project serves as a reference implementation with best practices specifically designed for:
- **GitHub Copilot** code suggestions and completions
- **AI-assisted development** with clear patterns and conventions
- **Large team collaboration** (50+ developers) with consistent code standards
- **Automated code generation** following established patterns

### Key AI/Copilot Features
- **📋 Standardized Code Patterns** - Consistent structure for predictable AI suggestions
- **🏷️ Rich Documentation** - Comprehensive comments and type annotations for AI context
- **🧪 Test-Driven Templates** - Clear testing patterns for AI to follow
- **🎯 Domain-Specific Language** - Business-focused naming and structure
- **📖 Schema-First API** - OpenAPI specifications drive AI code generation
- **🔧 Convention-Based Architecture** - Predictable file organization and naming

## 🏗️ Architecture Overview

This project demonstrates a hyper-scalable backend application with:

- **Domain-Driven Design (DDD)** - Clear separation of domain, application, and infrastructure layers
- **Event-Driven Architecture** - Kafka-based event publishing and consumption
- **API-First Approach** - OpenAPI 3.0 specification drives development
- **CQRS Pattern** - Command Query Responsibility Segregation
- **Clean Architecture** - Dependency inversion and clean boundaries
- **Comprehensive Testing** - Unit, Integration, API, and E2E tests with TestContainers
- **AI-Friendly Patterns** - Consistent naming and structure for AI code generation

## 🚀 Features

### Core Business Features
- **User Management** - CRUD operations with email validation and domain events
- **Order Management** - Full order lifecycle with status transitions and business rules
- **Event Publishing** - Domain events published to Kafka topics with proper serialization
- **Data Persistence** - PostgreSQL with Exposed ORM and connection pooling

### Technical Features
- **Swagger Documentation** - Auto-generated API docs at `/swagger-ui`
- **Health Checks** - Application health monitoring with dependency checks
- **Structured Logging** - Comprehensive logging with correlation IDs
- **Error Handling** - Global exception handling with proper HTTP status codes
- **CORS Support** - Cross-origin resource sharing configuration
- **Content Negotiation** - JSON serialization/deserialization with validation
- **Parallel Testing** - Fast unit tests and optimized integration tests
- **Java 21 Support** - Latest JVM features with coroutines integration

## 📁 Project Structure (AI/Copilot Optimized)

```
src/
├── main/kotlin/com/example/
│   ├── Application.kt                 # 🚀 Main entry point - startup configuration
│   ├── Module.kt                      # 🔧 Ktor module configuration
│   │
│   ├── api/                          # 🌐 REST API Layer (Controllers)
│   │   ├── controller/               # HTTP request handlers
│   │   │   ├── UserController.kt     # User CRUD operations
│   │   │   └── OrderController.kt    # Order lifecycle management
│   │   └── routes/                   # Route definitions and middleware
│   │
│   ├── application/                  # 🎯 Application Layer (Use Cases)
│   │   ├── dto/                     # Data Transfer Objects
│   │   │   ├── UserDtos.kt          # User API contracts
│   │   │   └── OrderDtos.kt         # Order API contracts
│   │   ├── service/                 # Application services (orchestration)
│   │   │   ├── UserApplicationService.kt
│   │   │   └── OrderApplicationService.kt
│   │   └── validation/              # Request validation rules
│   │
│   ├── domain/                      # 🧠 Domain Layer (Business Logic)
│   │   ├── model/                   # Domain entities and value objects
│   │   │   ├── User.kt              # User aggregate root
│   │   │   ├── Order.kt             # Order aggregate root
│   │   │   └── ValueObjects.kt      # Email, UserId, OrderId, etc.
│   │   ├── events/                  # Domain events for event sourcing
│   │   │   ├── UserEvents.kt        # UserCreated, UserUpdated
│   │   │   └── OrderEvents.kt       # OrderCreated, StatusChanged
│   │   ├── repository/              # Repository interfaces (ports)
│   │   │   ├── UserRepository.kt    # User data access contract
│   │   │   └── OrderRepository.kt   # Order data access contract
│   │   └── service/                 # Domain services (business rules)
│   │       ├── UserDomainService.kt # User business logic
│   │       └── OrderDomainService.kt# Order business logic
│   │
│   └── infrastructure/              # 🔧 Infrastructure Layer (Adapters)
│       ├── database/                # Database implementations
│       │   ├── DatabaseConfig.kt    # Connection setup and migrations
│       │   ├── Tables.kt            # Exposed table definitions
│       │   └── repository/          # Repository implementations
│       │       ├── UserRepositoryImpl.kt
│       │       └── OrderRepositoryImpl.kt
│       ├── messaging/               # Event streaming
│       │   ├── KafkaConfig.kt       # Kafka producer/consumer setup
│       │   ├── KafkaEventPublisher.kt
│       │   └── KafkaEventConsumer.kt
│       └── di/                      # Dependency injection
│           └── ApplicationModule.kt  # Dagger module configuration
│
├── test/kotlin/com/example/         # 🧪 Comprehensive Test Suite
│   ├── test/                        # Test utilities and base classes
│   │   ├── TestCategories.kt        # @FastTest, @IntegrationTest annotations
│   │   ├── fixtures/                # Test data builders and factories
│   │   │   ├── UserFixtures.kt      # Standardized user test data
│   │   │   ├── OrderFixtures.kt     # Standardized order test data
│   │   │   └── DtoFixtures.kt       # API contract test data
│   │   ├── utils/                   # Test utilities and helpers
│   │   │   ├── TestUtils.kt         # Common test operations
│   │   │   └── TestScenarios.kt     # Business workflow scenarios
│   │   └── base/                    # Base test classes for consistency
│   │       ├── BaseUnitTest.kt      # Unit test foundation
│   │       └── BaseIntegrationTest.kt# Integration test foundation
│   │
│   ├── domain/                      # 🎯 Domain Layer Tests (Unit Tests)
│   │   ├── model/                   # Entity and value object tests
│   │   │   ├── UserTest.kt          # User aggregate behavior
│   │   │   └── OrderTest.kt         # Order aggregate behavior
│   │   └── service/                 # Domain service tests with mocks
│   │       ├── UserServiceTest.kt   # Business logic validation
│   │       └── OrderServiceTest.kt  # Order workflow validation
│   │
│   ├── application/                 # 🎯 Application Layer Tests
│   │   └── service/                 # Use case testing with mocks
│   │
│   ├── api/                         # 🌐 API Layer Tests (HTTP Tests)
│   │   ├── UserApiIntegrationTest.kt # User endpoints testing
│   │   └── OrderApiIntegrationTest.kt# Order endpoints testing
│   │
│   └── integration/                 # 🔧 Integration Tests (TestContainers)
│       ├── FullModuleIntegrationTest.kt # End-to-end scenarios
│       ├── KafkaEventPublisherIntegrationTest.kt
│       └── EventHandlerIntegrationTest.kt
│
└── resources/
    ├── openapi/                     # 📋 API-First Specifications
    │   └── api.yaml                 # OpenAPI 3.0 specification
    ├── application.conf             # Ktor configuration
    └── logback.xml                  # Logging configuration
```

## 🤖 AI/Copilot Code Generation Patterns

### 1. Consistent Naming Conventions
```kotlin
// Domain Entities (AI Pattern: {Entity}+ attributes + behavior)
data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val createdAt: Instant
) {
    // Business methods follow pattern: verb + businessConcept
    fun updateProfile(firstName: String, lastName: String): User
    fun canPlaceOrder(): Boolean
}

// Value Objects (AI Pattern: validation + immutability)
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}

// Repository Interfaces (AI Pattern: suspend + CRUD + business queries)
interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: Email): User?
    suspend fun findActiveUsers(): List<User>
}

// Domain Services (AI Pattern: business rules + validation)
class OrderDomainService {
    suspend fun canUpdateStatus(order: Order, newStatus: OrderStatus): Boolean
    suspend fun calculateTotalAmount(items: List<OrderItem>): Double
}
```

### 2. Event-Driven Patterns
```kotlin
// Domain Events (AI Pattern: {Entity}{Action}Event + metadata)
@Serializable
sealed class DomainEvent {
    abstract val eventId: String
    abstract val aggregateId: String
    abstract val occurredAt: Instant
}

@Serializable
data class UserCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val aggregateId: String,
    override val occurredAt: Instant,
    val email: String,
    val firstName: String,
    val lastName: String
) : DomainEvent()

// Event Publisher (AI Pattern: async + error handling + serialization)
interface EventPublisher {
    suspend fun publish(event: DomainEvent)
    suspend fun publishBatch(events: List<DomainEvent>)
}
```

### 3. Test Patterns for AI Generation
```kotlin
// Test Class Pattern (AI recognizes structure)
@FastTest
@DisplayName("User Domain Model Tests")
class UserTest : BaseUnitTest() {
    
    @Nested
    @DisplayName("User Creation")
    inner class UserCreationTests {
        
        @Test
        fun `should create user with valid email`() {
            // Given (AI pattern: test data builders)
            val email = Email("test@example.com")
            val firstName = "John"
            val lastName = "Doe"
            
            // When (AI pattern: system under test action)
            val user = User.create(email, firstName, lastName)
            
            // Then (AI pattern: assertions + verification)
            user.email shouldBe email
            user.firstName shouldBe firstName
            user.lastName shouldBe lastName
            user.createdAt shouldNotBe null
        }
    }
}

// Integration Test Pattern (AI recognizes TestContainers setup)
@IntegrationTest
@DisplayName("User Repository Integration Tests")
class UserRepositoryIntegrationTest : BaseIntegrationTest() {
    
    @Test
    fun `should save and retrieve user from database`() = runTest {
        // Given
        val user = UserFixtures.aUser()
        
        // When
        val savedUser = userRepository.save(user)
        val retrievedUser = userRepository.findById(savedUser.id)
        
        // Then
        retrievedUser shouldNotBe null
        retrievedUser!!.email shouldBe user.email
    }
}
```

## 📋 API Endpoints (Schema-First Design)

All endpoints follow OpenAPI 3.0 specification for predictable AI code generation:

### Users API (`/api/v1/users`)
- `GET /api/v1/users` - List users (paginated, filterable)
- `POST /api/v1/users` - Create new user (with validation)
- `GET /api/v1/users/{id}` - Get user by ID (with error handling)
- `PUT /api/v1/users/{id}` - Update user profile (partial updates)

### Orders API (`/api/v1/orders`)
- `GET /api/v1/orders` - List orders (filterable by user/status)
- `POST /api/v1/orders` - Create new order (with business validation)
- `PUT /api/v1/orders/{id}/status` - Update order status (state machine)

### System Endpoints
- `GET /health` - Health check with dependency status
- `GET /` - API information and documentation links
- `GET /swagger-ui` - Interactive API documentation

## 📊 Observability & Health

### Metrics Endpoint
- **Path:** `/metrics`
- **Description:** Exposes Prometheus-compatible metrics for monitoring API performance and health.
- **Usage:**
    - Scrape with Prometheus or view directly: `curl http://localhost:8080/metrics`

### Health Check Endpoint
- **Path:** `/health`
- **Description:** Reports application readiness and liveness status for cloud-native orchestration (Kubernetes, etc).
- **Usage:**
    - Check health: `curl http://localhost:8080/health`

### Structured Logging
- **Correlation IDs:** Every request is assigned a correlation ID (`X-Request-ID` header or generated UUID) for traceability in logs.

## 🐳 Docker Usage

### Build Docker Image
```sh
./gradlew :ktor-server:shadowJar
cd ktor-server
docker build -t ktor-server:latest .
```

### Run Docker Container
```sh
docker run -p 8080:8080 ktor-server:latest
```

## 📈 Prometheus & Grafana Integration (Optional)
- Add Prometheus to your stack and configure it to scrape `/metrics`.
- Use Grafana to visualize metrics for API performance and health.

## 🧪 Testing Strategy (AI/Copilot Optimized)

### Test Categories for AI Pattern Recognition
1. **@FastTest** - Unit tests (< 5 seconds, no external dependencies)
2. **@IntegrationTest** - Integration tests (TestContainers, optimized)
3. **@ApiTest** - HTTP endpoint tests (full request/response cycle)
4. **@E2ETest** - End-to-end business scenarios

### AI-Friendly Test Commands
```bash
# Fast feedback loop for AI development
./gradlew unitTest --parallel              # Unit tests only (5 seconds)

# Full validation for AI-generated code
./gradlew integrationTest                  # Integration tests with containers

# Complete test suite
./gradlew test --parallel --max-workers=4  # All tests with parallel execution

# Test specific functionality
./gradlew test --tests "*User*"           # All user-related tests
./gradlew test --tests "*Integration*"    # All integration tests
```

## 🔧 Technologies Used (Latest Versions)

### Core Framework
- **Ktor 2.3.12** - Web framework with coroutines
- **Kotlin 2.0.20** - Programming language with latest features
- **Java 21** - Latest LTS JVM with modern features
- **Coroutines 1.9.0** - Asynchronous programming

### Database & ORM
- **PostgreSQL 15** - Primary database
- **Exposed 0.56.0** - Kotlin-native ORM
- **HikariCP 6.0.0** - High-performance connection pooling

### Messaging & Events
- **Apache Kafka 3.8.0** - Event streaming platform
- **Kafka Clients** - Producer/Consumer APIs with serialization

### Dependency Injection
- **Dagger 2.52** - Compile-time dependency injection

### Testing (Latest)
- **TestContainers 1.20.3** - Integration testing with real services
- **JUnit 5.11.3** - Modern testing framework
- **Mockito 5.14.2** - Mocking framework
- **Kotest 5.9.1** - Kotlin-specific assertions

### Documentation
- **OpenAPI 3.0** - API-first specification
- **Swagger UI** - Interactive documentation

## 🚀 Quick Start (AI/Copilot Development)

### Prerequisites
- **JDK 21+** (Amazon Corretto recommended)
- **Docker & Docker Compose** (for TestContainers)
- **IDE with AI support** (IntelliJ IDEA + GitHub Copilot)

### Development Setup

1. **Clone and build**:
```bash
git clone <repository-url>
cd SampleBackendKtorApp
./gradlew clean build
```

2. **Run fast tests** (AI development feedback):
```bash
./gradlew unitTest --parallel
```

3. **Run integration tests** (validate AI-generated code):
```bash
./gradlew integrationTest
```

4. **Start the application**:
```bash
./gradlew run
```

5. **Access the API**:
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui
- Health Check: http://localhost:8080/health

## 🎯 AI/Copilot Development Workflow

### 1. Schema-First API Development
1. Define API endpoints in `openapi/api.yaml`
2. Generate DTOs using OpenAPI specification
3. Implement controllers following established patterns
4. AI/Copilot will suggest consistent implementations

### 2. Domain-Driven Development
1. Start with domain models and value objects
2. Define business rules and validation
3. Create repository interfaces
4. AI/Copilot follows DDD patterns for suggestions

### 3. Test-Driven Development
1. Write tests using established patterns first
2. Use test fixtures and scenarios for consistency
3. Implement functionality with AI assistance
4. AI recognizes test patterns for better suggestions

### 4. Event-Driven Development
1. Define domain events for state changes
2. Implement event publishers and consumers
3. Use consistent serialization patterns
4. AI follows event-driven architecture patterns

## 🏷️ Code Generation Prompts for AI/Copilot

### Domain Model Generation
```
// Prompt: Create a Product domain entity with:
// - ProductId value object
// - Name, description, price fields
// - Business validation rules
// - Domain events for creation/updates
// Following the User/Order patterns in this project
```

### Repository Pattern Generation
```
// Prompt: Create ProductRepository interface and implementation
// Following the UserRepository pattern with:
// - Suspend functions for async operations
// - CRUD operations + business queries
// - Error handling and transaction support
```

### API Controller Generation
```
// Prompt: Create ProductController with REST endpoints
// Following the UserController pattern with:
// - OpenAPI annotations
// - Request/response DTOs
// - Validation and error handling
// - Business service integration
```

### Test Generation
```
// Prompt: Create comprehensive tests for Product
// Following existing test patterns with:
// - Unit tests with @FastTest
// - Integration tests with @IntegrationTest
// - Test fixtures and scenarios
// - Proper mocking and assertions
```

## 🔍 Key Design Patterns for AI Recognition

### 1. Repository Pattern
```kotlin
// Interface (Domain Layer)
interface UserRepository {
    suspend fun save(user: User): User
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: Email): User?
}

// Implementation (Infrastructure Layer)
class UserRepositoryImpl(private val database: Database) : UserRepository {
    override suspend fun save(user: User): User = // Implementation
}
```

### 2. Domain Events Pattern
```kotlin
// Event Definition
sealed class UserEvent : DomainEvent
data class UserCreatedEvent(...) : UserEvent()

// Event Publishing
class UserService(private val eventPublisher: EventPublisher) {
    suspend fun createUser(request: CreateUserRequest): User {
        val user = User.create(...)
        eventPublisher.publish(UserCreatedEvent(...))
        return user
    }
}
```

### 3. Value Objects Pattern
```kotlin
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID().toString())
    }
}
```

### 4. Application Service Pattern
```kotlin
class UserApplicationService(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {
    suspend fun createUser(request: CreateUserRequest): UserResponse {
        // Orchestration logic
    }
}
```

## 📚 Documentation

**Complete documentation is organized in the [`docs/`](docs/) directory.**

### Quick Links by Topic

#### 🧪 Testing Strategy
Comprehensive testing approach for enterprise-scale development:
- [Testing Strategy Overview](docs/testing-strategy/README.md)
- [Comprehensive Test Strategy](docs/testing-strategy/COMPREHENSIVE_TEST_STRATEGY.md)
- [Developer Testing Workflow](docs/testing-strategy/DEVELOPER_NARRATIVE_TESTING_FLOW.md)
- [Test Catalog & Registry](docs/testing-strategy/TEST_CATALOG.md)

#### 🎯 API-First Development
Schema-first approach with contract testing:
- [API-First Overview](docs/api-first/README.md)
- [API-First Roadmap](docs/api-first/API_FIRST_ROADMAP.md)
- [Enhanced OpenAPI Solution](docs/api-first/ENHANCED_OPENAPI_SOLUTION.md)

#### 🏗️ Implementation & Architecture
Technical details and project status:
- [Implementation Overview](docs/implementation/README.md)
- [Final Implementation Summary](docs/implementation/FINAL_IMPLEMENTATION_SUMMARY.md)
- [LKGB Status](docs/implementation/LKGB_STATUS.md)

#### 📖 Complete Documentation Hub
For a complete, organized view of all documentation:
- **[📚 Documentation Hub](docs/README.md)** - Start here for comprehensive documentation navigation

### Key Practices
- **Transaction Management:** All database integration tests use transactions for isolation and rollback
- **API Contract Versioning:** Automated backward compatibility validation against previous OpenAPI specs
- **Fast Feedback:** Unit tests complete in < 5 seconds for rapid development iteration
- **AI-Optimized:** Standardized patterns for predictable AI/Copilot code generation

## 📊 Performance & Scalability

### Build Performance
- **Parallel Gradle builds** - Multi-core utilization
- **Configuration cache** - Faster subsequent builds
- **Build cache** - Shared artifacts across team
- **Incremental compilation** - Only changed files recompiled

### Test Performance
- **Fast unit tests** - 5 seconds feedback loop
- **Shared containers** - TestContainers reuse for speed
- **Parallel test execution** - Multi-threaded test runs
- **Test categorization** - Run only relevant tests

### Runtime Performance
- **Coroutines** - Non-blocking async operations
- **Connection pooling** - Efficient database connections
- **Event streaming** - Kafka for async processing
- **Health monitoring** - Proactive performance tracking

## 📈 Code Quality & Best Practices

### AI/Copilot Enhancement Features
- **Consistent naming conventions** for predictable suggestions
- **Rich type information** for better AI context
- **Comprehensive documentation** for AI understanding
- **Standardized patterns** for consistent code generation
- **Test-driven approach** for AI validation

### Code Standards
- **Kotlin coding conventions** - Official style guide
- **Domain-driven design** - Business-focused code organization
- **Clean architecture** - Dependency inversion and separation
- **SOLID principles** - Maintainable and extensible code
- **Comprehensive testing** - Multiple test layers

### Security Considerations
- **Input validation** - All API endpoints validated
- **SQL injection prevention** - Parameterized queries
- **CORS configuration** - Proper cross-origin handling
- **Error handling** - No sensitive information leakage
- **Health checks** - System monitoring and alerting

## 🛡️ Production Readiness Checklist

### Monitoring & Observability
- ✅ **Structured logging** with correlation IDs
- ✅ **Health checks** for application and dependencies
- ✅ **Metrics collection** with proper instrumentation
- ✅ **Error tracking** with proper error boundaries

### Performance & Reliability
- ✅ **Connection pooling** for database efficiency
- ✅ **Graceful shutdown** handling
- ✅ **Retry mechanisms** for external service calls
- ✅ **Circuit breakers** for fault tolerance

### Security & Compliance
- ��� **Input validation** on all endpoints
- ✅ **SQL injection prevention**
- ✅ **CORS configuration**
- ✅ **Secure error handling**

### DevOps & Deployment
- ✅ **Containerization ready** (Docker support)
- ✅ **Environment configuration** management
- ✅ **Database migrations** with Exposed
- ✅ **CI/CD optimization** with test categorization

## 📚 Additional Resources

### AI/Copilot Learning Resources
- [GitHub Copilot Best Practices](https://docs.github.com/en/copilot)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Domain-Driven Design with Kotlin](https://kotlinlang.org/docs/server-overview.html)
- [Ktor Documentation](https://ktor.io/docs/)
- [TestContainers Documentation](https://www.testcontainers.org/)

### Development Tools
- **IntelliJ IDEA** with Kotlin plugin
- **GitHub Copilot** for AI-assisted development
- **Docker Desktop** for containerized services
- **Postman/Insomnia** for API testing
- **DBeaver** for database management

---

**Ready for AI/Copilot development with enterprise-grade patterns and comprehensive testing strategies.**

## 🧹 Clean Code Standards for AI/Copilot Development

### Core Clean Code Principles (AI-Optimized)

Our clean code standards are specifically designed to work with AI/Copilot tools, ensuring generated code follows enterprise-grade patterns:

#### 1. Single Responsibility Principle (SRP)
```kotlin
// ❌ Violates SRP - does validation, transformation, and persistence
class UserService {
    suspend fun createUser(request: CreateUserRequest): UserResponse {
        // validation logic
        if (request.email.isBlank()) throw ValidationException("Email required")
        if (!request.email.contains("@")) throw ValidationException("Invalid email")
        
        // transformation logic
        val userId = UUID.randomUUID().toString()
        val user = User(UserId(userId), Email(request.email), request.firstName, request.lastName)
        
        // persistence logic
        val savedUser = userRepository.save(user)
        eventPublisher.publish(UserCreatedEvent(savedUser.id.value, savedUser.email.value))
        
        // response building
        return UserResponse(savedUser.id.value, savedUser.email.value, savedUser.firstName, savedUser.lastName)
    }
}

// ✅ Follows SRP - each function has one responsibility
class UserApplicationService {
    suspend fun createUser(request: CreateUserRequest): UserResponse {
        val validatedRequest = validateCreateUserRequest(request)
        val userData = prepareUserData(validatedRequest)
        val savedUser = persistUser(userData)
        publishUserCreatedEvent(savedUser)
        return buildUserResponse(savedUser)
    }
    
    private fun validateCreateUserRequest(request: CreateUserRequest): CreateUserRequest {
        require(request.email.isNotBlank()) { "Email is required" }
        require(request.email.contains("@")) { "Invalid email format" }
        return request
    }
    
    private fun prepareUserData(request: CreateUserRequest): UserCreationData =
        UserCreationData(
            id = UserId.generate(),
            email = Email(request.email),
            firstName = request.firstName,
            lastName = request.lastName
        )
    
    private suspend fun persistUser(userData: UserCreationData): User {
        val user = User.create(userData.id, userData.email, userData.firstName, userData.lastName)
        return userRepository.save(user)
    }
    
    private suspend fun publishUserCreatedEvent(user: User) {
        val event = UserCreatedEvent(user.id.value, user.email.value, user.firstName, user.lastName)
        eventPublisher.publish(event)
    }
    
    private fun buildUserResponse(user: User): UserResponse =
        UserResponse(user.id.value, user.email.value, user.firstName, user.lastName)
}
```

#### 2. Function Names as Documentation (No Comments Rule)
```kotlin
// ❌ Uses comments instead of descriptive function names
class OrderService {
    suspend fun processOrder(orderId: OrderId): OrderResult {
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException()
        
        // Check if order can be processed
        if (order.status != OrderStatus.PENDING) {
            throw InvalidOrderStateException()
        }
        
        // Calculate total amount including taxes
        val subtotal = order.items.sumOf { it.price * it.quantity }
        val taxAmount = subtotal * 0.1
        val totalAmount = subtotal + taxAmount
        
        // Update order status and save
        val updatedOrder = order.copy(
            status = OrderStatus.PROCESSING,
            totalAmount = totalAmount,
            updatedAt = Clock.System.now()
        )
        
        return orderRepository.save(updatedOrder)
    }
}

// ✅ Uses descriptive function names instead of comments
class OrderService {
    suspend fun processOrder(orderId: OrderId): OrderResult {
        val order = findOrderById(orderId)
        validateOrderCanBeProcessed(order)
        val totalAmount = calculateTotalAmountWithTax(order)
        val processedOrder = markOrderAsProcessing(order, totalAmount)
        return saveOrder(processedOrder)
    }
    
    private suspend fun findOrderById(orderId: OrderId): Order =
        orderRepository.findById(orderId) ?: throw OrderNotFoundException()
    
    private fun validateOrderCanBeProcessed(order: Order) {
        require(order.status == OrderStatus.PENDING) { 
            "Order must be in PENDING status to process" 
        }
    }
    
    private fun calculateTotalAmountWithTax(order: Order): Double {
        val subtotal = calculateSubtotal(order.items)
        val taxAmount = calculateTaxAmount(subtotal)
        return subtotal + taxAmount
    }
    
    private fun calculateSubtotal(items: List<OrderItem>): Double =
        items.sumOf { it.price * it.quantity }
    
    private fun calculateTaxAmount(subtotal: Double): Double = subtotal * 0.1
    
    private fun markOrderAsProcessing(order: Order, totalAmount: Double): Order =
        order.copy(
            status = OrderStatus.PROCESSING,
            totalAmount = totalAmount,
            updatedAt = Clock.System.now()
        )
    
    private suspend fun saveOrder(order: Order): Order = orderRepository.save(order)
}
```

#### 3. Immutability and Functional Style
```kotlin
// ❌ Mutable operations and imperative style
class OrderStatusUpdater {
    suspend fun updateOrderStatuses(orders: List<Order>): List<Order> {
        val updatedOrders = mutableListOf<Order>()
        
        for (order in orders) {
            if (order.status == OrderStatus.PENDING && order.isExpired()) {
                val expiredOrder = order.copy(status = OrderStatus.EXPIRED)
                updatedOrders.add(expiredOrder)
            } else if (order.status == OrderStatus.PROCESSING && order.canBeShipped()) {
                val shippedOrder = order.copy(status = OrderStatus.SHIPPED)
                updatedOrders.add(shippedOrder)
            } else {
                updatedOrders.add(order)
            }
        }
        
        return updatedOrders
    }
}

// ✅ Immutable operations and functional style
class OrderStatusUpdater {
    suspend fun updateOrderStatuses(orders: List<Order>): List<Order> =
        orders.map { order -> updateOrderStatus(order) }
    
    private fun updateOrderStatus(order: Order): Order = when {
        shouldExpireOrder(order) -> expireOrder(order)
        shouldShipOrder(order) -> shipOrder(order)
        else -> order
    }
    
    private fun shouldExpireOrder(order: Order): Boolean =
        order.status == OrderStatus.PENDING && order.isExpired()
    
    private fun shouldShipOrder(order: Order): Boolean =
        order.status == OrderStatus.PROCESSING && order.canBeShipped()
    
    private fun expireOrder(order: Order): Order =
        order.copy(status = OrderStatus.EXPIRED, updatedAt = Clock.System.now())
    
    private fun shipOrder(order: Order): Order =
        order.copy(status = OrderStatus.SHIPPED, updatedAt = Clock.System.now())
}
```

#### 4. Self-Contained Data Classes for Related Information
```kotlin
// ❌ Multiple related parameters passed around
class VehicleService {
    suspend fun processVehicleUpdate(
        vehicleId: String,
        driverId: String,
        locationLat: Double,
        locationLng: Double,
        speed: Double,
        batteryLevel: Int,
        lastUpdated: Instant
    ): VehicleUpdateResult {
        // Complex logic with many parameters
    }
}

// ✅ Self-contained data classes group related information
class VehicleService {
    suspend fun processVehicleUpdate(updateData: VehicleUpdateData): VehicleUpdateResult {
        val validatedData = validateUpdateData(updateData)
        val processedData = processLocationData(validatedData)
        return saveVehicleUpdate(processedData)
    }
    
    private fun validateUpdateData(data: VehicleUpdateData): VehicleUpdateData {
        require(data.location.isValid()) { "Invalid location coordinates" }
        require(data.batteryLevel in 0..100) { "Battery level must be between 0-100" }
        return data
    }
    
    private fun processLocationData(data: VehicleUpdateData): VehicleUpdateData =
        data.copy(
            location = normalizeLocation(data.location),
            speed = calculateNormalizedSpeed(data.speed)
        )
    
    private suspend fun saveVehicleUpdate(data: VehicleUpdateData): VehicleUpdateResult {
        // Implementation details
    }
}

// Supporting data classes
data class VehicleUpdateData(
    val vehicleId: VehicleId,
    val driverId: DriverId,
    val location: GeoLocation,
    val speed: Double,
    val batteryLevel: Int,
    val timestamp: Instant
)

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
) {
    fun isValid(): Boolean = 
        latitude in -90.0..90.0 && longitude in -180.0..180.0
}
```

#### 5. Check vs. Act Separation
```kotlin
// ❌ Mixed check and action logic
class UserPermissionService {
    suspend fun removeUserIfUnauthorized(userId: UserId): Boolean {
        val user = userRepository.findById(userId) ?: return false
        
        if (user.lastLoginAt.isBefore(Clock.System.now().minus(90.days))) {
            userRepository.delete(userId)
            eventPublisher.publish(UserRemovedEvent(userId.value, "Inactive"))
            return true
        }
        
        return false
    }
}

// ✅ Separated check and action functions
class UserPermissionService {
    suspend fun removeInactiveUser(userId: UserId): UserRemovalResult {
        val user = findUserById(userId) ?: return UserRemovalResult.UserNotFound
        
        return if (shouldRemoveInactiveUser(user)) {
            removeUser(user)
            UserRemovalResult.Removed
        } else {
            UserRemovalResult.StillActive
        }
    }
    
    private suspend fun findUserById(userId: UserId): User? =
        userRepository.findById(userId)
    
    private fun shouldRemoveInactiveUser(user: User): Boolean =
        user.lastLoginAt.isBefore(Clock.System.now().minus(90.days))
    
    private suspend fun removeUser(user: User) {
        removeUserFromRepository(user.id)
        publishUserRemovedEvent(user.id)
    }
    
    private suspend fun removeUserFromRepository(userId: UserId) {
        userRepository.delete(userId)
    }
    
    private suspend fun publishUserRemovedEvent(userId: UserId) {
        val event = UserRemovedEvent(userId.value, "Inactive")
        eventPublisher.publish(event)
    }
}

enum class UserRemovalResult {
    UserNotFound, Removed, StillActive
}
```

### 🤖 AI/Copilot Clean Code Integration

#### AI-Friendly Refactoring Prompts

Use these standardized prompts with AI/Copilot for consistent code generation:

##### Domain Service Refactoring Prompt
```
Refactor this service class following these principles:
1. Single Responsibility - each method does exactly one thing
2. Function names as documentation - no inline comments
3. Immutability - use .copy() instead of mutation
4. Orchestration stages: validate → prepare → process → build response
5. Self-contained data classes for related parameters
6. Separate check and act functions
7. Functional style with map/filter instead of loops

Apply these principles to make the code AI/Copilot friendly and maintainable.
```

##### Repository Implementation Prompt
```
Create a repository implementation that follows:
1. Single responsibility for each method
2. Descriptive method names explaining business intent
3. Immutable data handling with proper error types
4. Separate query construction from execution
5. Transaction management as separate concerns
6. Functional composition for complex queries

Make it consistent with existing repository patterns for AI recognition.
```

##### API Controller Refactoring Prompt
```
Refactor this controller following:
1. Single responsibility - one concern per endpoint method
2. Clear validation, processing, and response building stages
3. No business logic in controllers (orchestration only)
4. Immutable request/response handling
5. Descriptive private methods instead of comments
6. Proper error handling with meaningful HTTP responses

Ensure AI/Copilot can recognize and replicate the patterns.
```

#### Code Review Checklist for AI-Generated Code

When reviewing AI/Copilot generated code, verify:

✅ **Single Responsibility**
- [ ] Each function handles exactly one concern
- [ ] Large methods are broken into focused private functions
- [ ] Classes have a single reason to change

✅ **Self-Documenting Code**
- [ ] Function names clearly explain what they do
- [ ] No inline comments explaining business logic
- [ ] Business rules expressed as named functions

✅ **Immutability**
- [ ] Data classes updated using `.copy()` instead of mutation
- [ ] Functional style used instead of mutable collections
- [ ] Return new objects rather than modifying existing ones

✅ **Clear Architecture**
- [ ] Orchestration stages are clearly separated
- [ ] Check and act functions are separate
- [ ] Related data grouped in self-contained data classes

✅ **AI-Friendly Patterns**
- [ ] Consistent naming conventions
- [ ] Predictable structure for AI recognition
- [ ] Clear separation of concerns for better suggestions

### 🛡️ Security-Focused Clean Code Patterns

#### Input Validation with Clean Code Principles
```kotlin
// ✅ Clean code approach to security validation
class SecureUserValidator {
    fun validateCreateUserRequest(request: CreateUserRequest): ValidationResult {
        return validateAllFields(request)
            .flatMap { validateBusinessRules(it) }
            .flatMap { validateSecurityConstraints(it) }
    }
    
    private fun validateAllFields(request: CreateUserRequest): ValidationResult =
        sequenceOf(
            validateRequiredFields(request),
            validateFieldFormats(request),
            validateFieldLengths(request)
        ).fold(ValidationResult.success(request)) { acc, validation ->
            acc.flatMap { validation }
        }
    
    private fun validateRequiredFields(request: CreateUserRequest): ValidationResult =
        when {
            request.email.isBlank() -> ValidationResult.failure("Email is required")
            request.firstName.isBlank() -> ValidationResult.failure("First name is required")
            request.lastName.isBlank() -> ValidationResult.failure("Last name is required")
            else -> ValidationResult.success(request)
        }
    
    private fun validateFieldFormats(request: CreateUserRequest): ValidationResult =
        when {
            !isValidEmailFormat(request.email) -> ValidationResult.failure("Invalid email format")
            containsInvalidCharacters(request.firstName) -> ValidationResult.failure("First name contains invalid characters")
            containsInvalidCharacters(request.lastName) -> ValidationResult.failure("Last name contains invalid characters")
            else -> ValidationResult.success(request)
        }
    
    private fun validateSecurityConstraints(request: CreateUserRequest): ValidationResult =
        when {
            containsMaliciousContent(request.email) -> ValidationResult.failure("Invalid email content")
            containsMaliciousContent(request.firstName) -> ValidationResult.failure("Invalid first name content")
            containsMaliciousContent(request.lastName) -> ValidationResult.failure("Invalid last name content")
            else -> ValidationResult.success(request)
        }
    
    private fun isValidEmailFormat(email: String): Boolean = 
        EMAIL_REGEX.matches(email)
    
    private fun containsInvalidCharacters(text: String): Boolean =
        INVALID_CHARACTERS_REGEX.containsMatchIn(text)
    
    private fun containsMaliciousContent(text: String): Boolean =
        MALICIOUS_PATTERNS.any { pattern -> pattern.containsMatchIn(text) }
    
    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val INVALID_CHARACTERS_REGEX = Regex("[<>\"'&]")
        private val MALICIOUS_PATTERNS = listOf(
            Regex("(?i)<script"),
            Regex("(?i)javascript:"),
            Regex("(?i)on\\w+\\s*=")
        )
    }
}

sealed class ValidationResult<out T> {
    data class Success<T>(val value: T) : ValidationResult<T>()
    data class Failure(val error: String) : ValidationResult<Nothing>()
    
    fun <R> flatMap(transform: (T) -> ValidationResult<R>): ValidationResult<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }
    
    companion object {
        fun <T> success(value: T): ValidationResult<T> = Success(value)
        fun failure(error: String): ValidationResult<Nothing> = Failure(error)
    }
}
```
