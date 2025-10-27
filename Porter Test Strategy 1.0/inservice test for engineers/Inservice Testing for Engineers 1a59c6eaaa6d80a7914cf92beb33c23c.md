# Inservice Testing for Engineers

### Problem Statement:

1. Inservice Testing not a standard practice for developers
    1. Guidelines not consistently followed the ORG
        1. Test are concurrent (class/method)
    2. Guidelines available agreed upon by PEs overall but adoption is not there
    3. Quality of Tests being written by developers
    4. Performance of tests
    5. Multiple test libraries are being used - spek, junit, cucumber, etc
    6. Test framework is not consistent for junit tests across repos
    7. No local coverage report (consistent with sonar report)
    8. Coverage for few services only
2. Inconsistent test coverage for services where tests are present
    1. BrokenTests & Flaky Tests
    2. Local Test Environment Setup
    3. No Sprint estimation for writing tests
        1. Visual coverage feedback to developers is missing
3. Inconsistent Test Integration in CI pipelines - Reject build if the test fails
    1. Broken Sonar Pipelines
    2. Test Pipelines take too long to execute

### Testing as a Mental Model

Developers and EMs should consider testing an **integral part of development** rather than an afterthought. This mindset ensures that the code is **reliable, maintainable, and scalable** while reducing regressions and improving overall software quality.

Testing should be about **building confidence in the codebase rather than just writing test cases**. Developers should approach testing with the same rigor as writing application logic, ensuring that all critical components function as expected under various conditions.

## **Key Principles of Testing as a Mental Model**

1. **Test in Isolation Before Integration**
    - **Microservices should be tested in isolation** to verify correctness without dependencies on external systems.
    - This simplifies debugging, reduces test flakiness, and ensures that services work **independently** before being tested in a larger ecosystem.
2. **Layered Testing Approach**
    - Different layers of the application require different types of tests to ensure complete coverage:
        - **Unit Tests 🟢** : Verify individual components (API layer, domain, data).
        - **Integration Tests  🔄** : Validate interaction between components (e.g., HTTP services, event processing, database interactions).
3. **Sociable vs. Solitary Tests**
    - **Sociable Tests**: Validate real interactions between components to test **real-world behavior**.
    - **Solitary Tests**: Use **test doubles (mocks, stubs, fakes)** to isolate a component’s logic from its dependencies.
    - A **balanced approach** ensures both correctness (solitary tests) and integration reliability (sociable tests).
4. **Testing as a Safety Net for Refactoring**
    - **Comprehensive test coverage enables fearless refactoring.**
    - Changes in business logic should be backed by **robust unit and integration tests** to prevent regressions.
5. **Mocking vs. Real Dependencies**
    - Use **TestContainers** for infrastructure dependencies (Databases, Redis, Kafka).
    - Mock external services (SQS, HTTP clients) to improve test reliability.
    - Avoid excessive mocking of **internal application logic** to keep tests meaningful.
6. **Test Design Best Practices**
    - Follow the **Arrange-Act-Assert (AAA)** pattern to keep tests structured and readable.
    - Tests should have **clear and descriptive names** (e.g., `shouldReturn400WhenInputIsInvalid`).
    - Ensure **minimal dependencies** to avoid test flakiness.
    - Write tests **that simulate real-world scenarios** rather than just checking code execution paths.
7. **Continuous Testing & CI/CD Integration**
    - Tests should be part of every commit—**automated testing should run on CI/CD pipelines**.
    - Build failures should occur if tests fail or coverage drops below a defined threshold.
    - Flaky tests should be **monitored, logged, and proactively fixed** to avoid noise in pipelines.

## Applying the Testing Mindset

1. **For Unit Tests**
    - Test **public methods** of domain and validation classes.
    - Mock external service interactions but not internal domain logic.
    - Ensure test assertions verify both **output correctness** and **behavioral expectations** (e.g., method invocations).
2. **For Integration Tests**
    - Validate API responses, database interactions, and event processing.
    - Use **TestContainers** for database and cache operations instead of mocks.
    - Ensure integration tests **simulate real-world interactions** with dependent services.
3. **For Controller & API Tests**
    - Tests should **invoke endpoints** rather than directly calling service methods.
    - Validate all possible **HTTP responses (success, errors, validation failures, timeouts)**.
    - Use **WireMock** for mocking external HTTP calls.
4. **For Event-Driven Services (SQS, Kafka)**
    - Test **event consumption and processing logic** in isolation.
    - Mock event publishers but **test event consumers with real dependencies** (TestContainers).
    - Ensure that system state updates (DB, cache) are properly validated.

### Project Structure:

This is a microservices-based project following Domain-Driven Design (DDD) with multiple modules:

1. **Core Modules**:
    - `orders` - Order management
    - `events` - Event handling
    - `life-cycle` - Order lifecycle management
    - `vas` (Value Added Services) - Additional services like delivery notes
    - `ktor` - Web server implementation
2. **Module Structure** (per feature):
    
    ```
    feature/
    ├── api/              # (includes Controller)
    │   ├── apimodels/    # API DTOs - Data transfer object
    │   └── service/      # API implementation
    ├── domain/           # Business logic
    └── data/             # (Data layer)
        ├── psql/         # PostgreSQL implementation
        └── fused/        # Data layer abstraction
    
    ```
    

### Testing Layers:

![testing_strategy_new_1.png](Inservice%20Testing%20for%20Engineers%201a59c6eaaa6d80a7914cf92beb33c23c/testing_strategy_new_1.png)

1. **Domain Layer Tests**
    - Unit tests for business logic
        - Example: `PostLifeCycleExecutorSpek.kt`
    - Focus on use cases and business rules
2. **Mapper Tests**
    - Tests for DTO-to-Domain conversions
        - Examples:
            
            ```kotlin
            - TripDeliveryNoteDetailsMapperTest.kt
            - ObjectUploadUrlMapperTest.kt
            
            ```
            
3. **API Layer Tests**
    - Controller/endpoint testing
        - exception handling tests
        - Garbage data tests
        - couple of scenarios around 200
        - Response Data validation
    - Request/response validation
    - Integration with domain layer
4. **Data Layer Tests**
    - Repository implementations
    - Database operations
    - Uses TestContainers for integration testing

### Test Coverage Requirements

From `CONTRIBUTING.md`:

- Minimum 92% code coverage required
- Uses SonarQube for coverage measurement
- Integration with DataDog for monitoring

### Testing Tools Available

From the dependencies:

```kotlin
testImplementation(OrderSystemLibs.TestContainers.testContainers)
testImplementation(OrderSystemLibs.TestContainers.postgresSql)
testImplementation(OrderSystemLibs.TestContainers.juniper)

```

### Suggested Testing Approach

1. Start with Unit Tests:
    - Write tests for mappers
    - Test domain logic
    - Use mocking for dependencies
2. Integration Tests:
    - Test database operations using TestContainers
    - API endpoint integration tests
    - Event handling tests
3. Test Infrastructure:
    
    ```yaml
    - Redis (port 6379)
    - PostgreSQL (port 5431)
    - Kafka (port 9092)
    - Schema Registry (port 8081)
    
    ```
    

Use the project's existing test patterns as examples when writing new tests.

### Vision & End Goals:

1. To have automated UT, cross Service tests and Mobile tests performed (3 different areas)
2. Releasing and deploying apps(Docker container + ArgoCD apps) to prod in a automated way based on test results
3. Having robust automation suite coverage for UT, Module Integration and Cross service integration tests which can help build confidence among stakeholders for quality being delivered.
4. Having a system where we test a build on staging environment first using masterbox and then rolling out the same to production
    1. which is reverse of current state i.e. in current system builds are deployed to production in every 15mins using cron schedulers - WIP
    2. then first deployment happens in Prod env, and then in staging env - WIP
5. Roadmap is WIP and few of the initiatives are already in motion, which are concurrent and independent milestones

### OKRs for **In-Service Testing:**

## **Objective:**

**“Establish a robust in-service testing framework (unit + integration) that ensures high-quality, reliable, and easily maintainable code, with clear standards, local test execution, and proactive management of flaky tests.”**

### **Key Results**

1. **Publish Testing Standards**
    - **KR**: Define and roll out a standardized guideline for unit and integration testing (including coding conventions, mocking best practices, and coverage expectations) by **end of Q2**, with 100% of developers acknowledging receipt and training completion. (e.g. Base Setups)
2. **Local Test Coverage Parity**
    - **KR**: Achieve a discrepancy of **<5%** between local code coverage and CI/CD-reported coverage across **90%** of services by **end of Q3**.
3. **Increase Unit Test Coverage**
    - **KR**: Raise overall unit test coverage from **(current baseline)%** to **(target)%** across all critical services by **end of Q3**.
4. Fix issues with Local Setup for unit/integration inservice tests
    - KR: Find out issues faced during local setup e.g. Colima configuration issues. Create and publish document with solutions
5. **Reduce Flaky/Broken Tests**
    - **KR**: Implement automated flaky test detection and fix processes, reducing the number of flaky or broken tests by **50%** from the current baseline by **end of Q3**.
6. **Establish Peer Review for Test Quality**
    - **KR**: Launch a peer review checklist for new and updated test cases, ensuring **90%** of merged test code meets coverage, reliability, and standard guidelines by **end of Q2**.
7. **Incorporate Test-Writing Estimates**
    - **KR**: Ensure **80%** of new user stories or tasks include separate test-writing estimates in sprint planning by **end of Q4**.
8. **Stabilize and Optimize Sonar Pipeline**
    - **KR**: Implement improvements to the Sonar pipeline to reduce average scan time by **20%** from the current baseline, while maintaining **≥98%** pipeline stability (no coverage or scan failures) across all critical services by **end of Q3**.

## Testing Structure

### **Base Test Setup**

[https://excalidraw.com/#json=1bJvlxSp7tkn_iJL1DwVA,NwFYB_W77Dqq99P3tAF46A](https://excalidraw.com/#json=1bJvlxSp7tkn_iJL1DwVA,NwFYB_W77Dqq99P3tAF46A)

### **`BaseRepoTest` (Core Test Setup for Database, Redis)**

- Defines the **test database and Redis setup**.
- Injects required dependencies using **Dagger (`RepoTestComponent`)**.
- Runs **setup and teardown** logic before and after test execution.
- Every repository test inherits from this base class.

### **`BaseHttpServiceIntegrationTest` (Ktor Route Testing)**

- Extends `BaseRepoTest` to include HTTP-related components.
- Uses **TestApplicationEngine** to run an in-memory Ktor instance.
- Injects **TestAppComponent** for API dependencies.
- Used for testing **Ktor API routes**.

### **`Base<ModuleName>PsqlTestRepo` (PostgreSQL-Based Repository Testing)**

- Extends `BaseRepoTest` to set up **PostgreSQL schema and test queries**.
- Runs `SchemaUtils.create()` before tests and `SchemaUtils.drop()` after tests.

### **`Base<ModuleName>RedisTest` (Redis-Based Repository Testing)**

- Extends `BaseRepoTest` to test **Redis-based caching**.

### Testing Strategy Guidelines

[Guidelines for modules](https://www.notion.so/4b442fecd8bc471a82d9e38d5c82a5a0?pvs=21)

### Code Examples:

### **1. Base Test Class (Common for All Tests)**

This class sets up **database, Redis, and dependency injection**.

```kotlin
TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseRepoTest {

    lateinit var repoTestComponent: RepoTestComponent

    @Inject
    lateinit var dataSource: HikariDataSource

    @Inject
    lateinit var databaseSelector: DatabaseSelector

    @Inject
    lateinit var database: Database

    @Inject
    lateinit var redisClient: RedisClient

    @BeforeAll
    fun setup() {
        repoTestComponent = DaggerRepoTestComponent.builder()
            .build()
        repoTestComponent.inject(this)
    }
}
```

---

### **2. Ktor Route Testing**

### **Base Class for Route Testing**

This class sets up the **Ktor test engine** and initializes the app.

```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseHttpServiceIntegrationTest : BaseRepoTest() {

    lateinit var testAppComponent: TestAppComponent

    @BeforeAll
    fun init() {
        testAppComponent = DaggerTestAppComponent.builder().build()
    }
}
```

### **Example Test Case for Ktor Route**

```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferralSystemRoutesTest : BaseHttpServiceIntegrationTest() {

    @BeforeAll
    fun initTest() {
        testApplicationEngine = InitTestEngine.invoke(testAppComponent)
        httpClientEngine = TestHttpClientEngine.create { app = testApplicationEngine }
    }

    @Test
    fun `should return referral attempts count successfully`() = runTest {
        val response = referralSystemClient.getReferralAttemptsCount(testCustomerId, startDate, Instant.now())
        Assertions.assertEquals(response, 2)
    }
}

```

---

### **3. PostgreSQL (Data Layer) Testing**

### **Base Class for PostgreSQL Tests**

```kotlin

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseDiscountSystemPsqlTestRepo : BaseRepoTest() {

    val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val tables = arrayOf(
        CampaignsTable, ComputersTable, ValidationsTable, WhiteListedCustomersTable,
        CustomersTable, OrdersTable, DevicesTable, CustomerCompletedOrdersTable,
        CustomerOrdersTable, CustomerOrderAggTable, TranslationTable, AdministratorsTable
    )

    @BeforeAll
    fun init() {
        repoTestComponent.inject(this)
        transaction(database) { SchemaUtils.create(*tables) }
    }

    @AfterAll
    fun cleanup() {
        transaction(database) { SchemaUtils.drop(*tables) }
    }

```

### **Example Test Case for PostgreSQL**

```kotlin
class CampaignQueriesTest : BaseDiscountSystemPsqlTestRepo() {

    private lateinit var campaignQueries: CampaignQueries

    @BeforeEach
    fun setUp() {
        campaignQueries = CampaignQueries(testCoroutineDispatcher, databaseSelector, ActivationStatusMapper(), CouponStatusMapper(), CRMCampaignMapper())
    }

    @Test
    fun `createCampaign should return list of campaign ids`() = runBlocking {
        val campaignIds: List<Int> = campaignQueries.createCampaign(couponDetails)
        assert(campaignIds.isNotEmpty())
    }
}

```

---

### **4. Redis (Data Layer) Testing**

### **Base Class for Redis Tests**

```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseRewardsRedisTest : BaseRepoTest() {

    @BeforeAll
    fun init() {
        repoTestComponent.inject(this)
    }
}

```

### **Example Test Case for Redis**

```kotlin

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisRewardsRepoTest : BaseRewardsRedisTest() {

    private lateinit var redisRepo: RedisRewardsRepo
    private lateinit var lettuceRedisClient: LettuceRedisClient

    @BeforeAll
    fun setupDependencies() {
        redisRepo = RedisRewardsRepo(LettuceRedisClient(redisClient))
        lettuceRedisClient = LettuceRedisClient(redisClient)
    }

    @Test
    fun shouldInsertInCache() = runTest {
        val uuid = UUID.randomUUID()
        redisRepo.setMigrationProcessed(uuid)
        assert(redisRepo.getMigrationProcessed(uuid))
    }

    @Test
    fun shouldDeleteFromCache() = runTest {
        val uuid = UUID.randomUUID()
        redisRepo.setMigrationProcessed(uuid)
        redisRepo.delMigrationProcessed(uuid)
        assert(!redisRepo.getMigrationProcessed(uuid))
    }
}

```

Implementation References:

- BaseRepoTest
    - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/commons/domain/src/testFixtures/kotlin/in/porter/hogsmeade/commons/domain/BaseRepoTest.kt#L13
- Route Testing:
    - BaseHttpServiceIntegrationTest
        - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/servers/ktor/server/src/test/kotlin/fixtures/BaseHttpServiceIntegrationTest.kt#L8
    - Route Test class inheriting from  BaseHttpServiceIntegrationTest
        - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/servers/ktor/server/src/test/kotlin/in/porter/hogsmeade/ktor/server/CashbackRoutesTest.kt#L45
- Testing data Layer (testing transactions):
    - Base Module Repo psql repo class
        - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/discount-system/data/psql/src/test/kotlin/in/porter/hogsmeade/discountsystem/data/psql/BaseDiscountSystemPsqlTestRepo.kt#L24
    - Query class inheriting from Module Repo class
        - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/discount-system/data/psql/src/test/kotlin/in/porter/hogsmeade/discountsystem/data/psql/administrators/repos/CampaignQueriesTest.kt#L13
    - Redis Module Repo
        - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/rewards/data/redis/src/test/kotlin/in/porter/hogsmeade/rewards/data/redis/BaseRewardsRedisTest.kt#L8
    - Redis module test class
        - ‣https://github.com/porterin/hogsmeade/blob/064d1b1fe76ccc5b13a4a3d0db8f6ad67ee44d56/rewards/data/redis/src/test/kotlin/in/porter/hogsmeade/rewards/data/redis/RedisRewardsRepoTest.kt#L12
- Domain Layer:
    - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/discount-system/domain/src/test/kotlin/in/porter/hogsmeade/discountsystem/domain/campaigns/usecases/administration/CreateCouponCampaignTest.kt#L19
- API layer:
    - https://github.com/porterin/hogsmeade/blob/1594c37b3726e51fcb05094dad41624a122c0505/discount-system/api/service/src/test/kotlin/in/porter/hogsmeade/discountsystem/api/service/campaigns/usecases/CouponDetailsMapperTest.kt#L12