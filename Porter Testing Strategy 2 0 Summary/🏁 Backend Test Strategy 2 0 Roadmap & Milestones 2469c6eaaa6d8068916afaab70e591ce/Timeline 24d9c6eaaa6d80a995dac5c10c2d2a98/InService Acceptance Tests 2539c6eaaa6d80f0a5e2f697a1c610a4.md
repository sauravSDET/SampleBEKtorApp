# InService Acceptance Tests

Comments: 1. Identification of API acceptance tests
2. Writing acceptance tests at HTTP route layer
3. UpStream Dependencies are mocked
4. For downstream dependencies use test containers and mocks
5. No network communication required utilize ATS
6. Explore if BDD Test cases are required
7. Traceability of acceptance against each service e.g. Using Test Rail
Theme: InService Acceptance Tests
Timeline: September 30, 2025
priority: P0

**Problem Statement:** 

Acceptance test should run for all the changes made by developers in their Local Setup itself giving faster feedback on feature correctness. 

**Outcome:** 

1. Identification of API acceptance tests e.g. for Fury
2. Writing acceptance tests at HTTP route layer e.g. using Junit and KTOR
3. UpStream Dependencies are mocked
4. Downstream dependencies use test containers and mocks
5. No network communication required utilize Ktor Application Test Server (ATS)
6. Explore if BDD Test cases are required e.g. Cucumber, RestAssured
7. Traceability of acceptance against each service e.g. Using Test Rail
8. POC on DataDriven or Parameterized tests

**Approaches:** 

To write InService **acceptance** test cases, Depending upon who will be writing the test scenarios and test cases there are two ways of moving forward:

1. Stick to current Junit tests (will need to upgrade to Junit5 across org to support ParameterizedTest)
    1. Make tests data driven i.e. input requests and output response combinations are stored at central location
    2. Write utils to read test data from central location (GCS/GoogleSheets) OR this data combination can exist in test part of the service itself
    3. Complex to read for a PM/PO
2. If Product managers are supposed to Write/Review the tests then we should Pick some language which to make the test more readable than native Junit tests
    1. **RestAssured** (supports Gherkin type syntax and test runner is still Junit)
    2. Use **Cucumber** -> Native Gherkin support but Overhead of writing and maintaining StepDefinitions and Glues
3. Traceability of the test can be done in test rail OR BrowserStack Test Management (POC in progress in PP)
    1. OR ignore test rail if central location is good enough to agree on the coverage -> PO/PMs can pitch in for this decision

P.s:

1. Above 2.a. methodology is already being used for SIT automation i.e. "TestNG + DataProviders + RestAssured"
2. Performance wise, RestAssured lacks behind when compared to Apache HttpClient as it's a DSL over Apache HttpClient

Approaching this from ease of adoption POV:

1. Low dev effort in writing and maintaining these tests
2. Delivering this feature quickly and with least amount of change to current framework
3. Not sure yet that, when CDC is adopted then maintaining separate test data will be additional overhead or not

**Milestones:** 

[InService Acceptance Test Milestones](InService%20Acceptance%20Tests%202539c6eaaa6d80f0a5e2f697a1c610a4/InService%20Acceptance%20Test%20Milestones%202539c6eaaa6d8019955dd5f31f518f38.csv)