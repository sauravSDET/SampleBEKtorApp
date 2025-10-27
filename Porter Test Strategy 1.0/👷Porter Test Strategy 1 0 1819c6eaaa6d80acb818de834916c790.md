# 👷Porter Test Strategy 1.0

Author: Saurav Kumar
Ver 2.0: Porter Testing Strategy 2.0 Summary: (https://www.notion.so/Porter-Testing-Strategy-2-0-Summary-2269c6eaaa6d803f993fec053681ba02?pvs=21)

## Problem Statement

Our current testing approach has several gaps and inconsistencies across different levels of the application stack. Developers face challenges such as Inconsistent local testing practices, Fragmented testing frameworks, Variable test quality and Lack of standardised coverage reporting. Also, Tests are not uniformly integrated into CI/CD pipelines, and end-to-end tests for mobile or web UI are not fully automated or consistently executed. 

The result is a system where builds may pass with unreliable tests or no tests coverage for many modules/features, making it difficult to confidently deploy high-quality, maintainable code. Our goal is to establish a testing framework that builds confidence across the organisation—ensuring every change is validated through well-defined unit (in-service) tests, robust cross-service API tests with real dependencies, and comprehensive mobile/web UI end-to-end tests.

## Initiatives by Testing Category

### 1. **Unit Tests / In-Service Tests (incl. integration tests):**

Ref: [Inservice Testing for Engineers](https://www.notion.so/Inservice-Testing-for-Engineers-1a59c6eaaa6d80a7914cf92beb33c23c?pvs=21) 

**Objective:**

Establish a standardised in-service testing framework that ensures developers view testing as an integral part of development. This initiative targets improved local testing consistency, reliable unit test execution, and higher code quality through better test design and adherence to standards.

**Initiatives:**

- **Standardised Testing Guidelines:**
    - Publish clear guidelines for unit testing (coding conventions, AAA pattern, naming, and minimal dependency principles).
    - Mandate that tests cover public methods, domain logic, and validations.
    - Ensure 100% acknowledgment and training for developers on these guidelines.
- **Consistent Test Framework Adoption:**
    
    Ref: ‣ 
    and 
    
    - Consolidate unit test libraries (e.g., JUnit, Spek) across repositories.
    - Standardise mocking practices using frameworks like Mockk.
    - Define a common test execution pattern to reduce flakiness and improve performance.
- **Local Test Environment & Coverage:**
    
    Ref: [🚀 **Increase Code Coverage Locally with JaCoCo**](%F0%9F%91%B7Porter%20Test%20Strategy%201%200%201819c6eaaa6d80acb818de834916c790/%F0%9F%9A%80%20Increase%20Code%20Coverage%20Locally%20with%20JaCoCo%201969c6eaaa6d80c4ab5bfdfab86985c6.md) 
    
    - Address local environment setup issues (e.g., Colima configurations, TestContainers integration).
    - Provide a documented solution for environment inconsistencies.
    - Implement local code coverage reporting that aligns with CI/CD (e.g., SonarQube via Jacoco).
- **Test Quality & Maintenance:**
    - Introduce a peer-review checklist for all new and updated tests.
    - Implement automated detection for flaky and broken tests, targeting a X% reduction from the current baseline.
    - Set clear coverage requirements (e.g., minimum X% coverage as per CONTRIBUTING.md).
- **Service Integration:**
    - Introduce tests for event-driven services (e.g., SQS, Kafka) ensuring events are consumed and processed correctly.
    - Verify system state changes (in databases, caches) as part of the integration test suite.
    - Consolidate jacoco reports across services to have a unified view of integration test coverage.
- **Build and Performance Optimisation:**
    - Investigate and prototype a breakup of the single-threaded build/test phase.
    - Explore parallel test execution to reduce overall build time.

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

### Vision & End Goals:

1. To have automated UT, cross Service tests and Mobile tests performed (3 different areas)
2. Releasing and deploying apps(Docker container + ArgoCD apps) to prod in a automated way based on test results
3. Having robust automation suite coverage for UT, Module Integration and Cross service integration tests which can help build confidence among stakeholders for quality being delivered.
4. Having a system where we test a build on staging environment first using masterbox and then rolling out the same to production
    1. which is reverse of current state i.e. in current system builds are deployed to production in every 15mins using cron schedulers - WIP
    2. then first deployment happens in Prod env, and then in staging env - WIP
5. Roadmap is WIP and few of the initiatives are already in motion, which are concurrent and independent milestones

---

### 2. **Cross Service Tests / API Tests with Real Dependencies:**

Ref: [**API Test Automation Trigger via Flash:**](https://www.notion.so/API-Test-Automation-Trigger-via-Flash-1bb9c6eaaa6d80dda0f5fe3497b3e43f?pvs=21) 

**Objective:**

Improve the reliability and integration quality of services by conducting tests that simulate real-world scenarios with actual dependencies. This includes robust API testing, cross-service interactions, and integration tests that validate end-to-end service behavior.

**Initiatives:**

- **Real Dependency Integration:**
    - Transition from heavy mocking to using real dependencies where possible, utilising Real DB (TestContainers in case of DB changes) for databases (Postgres, Redis) and caches. P.s. On demand DB isolation is precondition for this.
    - Adopt tools like WireMock for realistic external HTTP interactions during API tests. → Can be done in UT, but
- **API & Integration Testing:**
    - Develop tests that validate API endpoints by triggering real deployments on staging environments.
    - Cover all scenarios including success, error, and edge cases (e.g., invalid inputs, timeouts, rate limiting).
    - Utilise existing service test patterns and extend them to cover cross-service dependencies.
- **System Integration:**
    - Introduce tests for event-driven services (e.g., SQS, Pub/Sub, Kafka) ensuring events are consumed and processed correctly across services e.x. events generated by order system are consumed properly by fury.
    - Verify system state changes (in databases, caches) as part of the integration test suite.
- **CI/CD Pipeline Integration:**
    - Enforce that builds are rejected if integration tests fail.
    - Enhance the Sonar pipeline to reduce scan times by X% while maintaining high stability (≥X%).
    - Include clear, automated feedback loops for developers when tests fail or coverage drops.

---

### 3. **Mobile Application / Web UI End-to-End Tests:**

Ref: [TestRail data cleanup approach document:](https://www.notion.so/TestRail-data-cleanup-approach-document-1579c6eaaa6d803dbd38c30ed6d12b47?pvs=21) 

**Objective:**

Develop a comprehensive end-to-end (E2E) test suite for mobile applications and web UIs that ensures a seamless user experience. This initiative targets the full automation of UI testing, from triggering tests to detailed reporting, to catch issues that may be missed at the unit or API level.

**Initiatives:**

- **Automation of E2E Testing using Appium -** it uses UIautomator2 internally for android and XCUITest for iOS 
****https://developer.android.com/training/testing/other-components/ui-automator#java
    - Implement fully automated E2E tests for both mobile (Android & iOS) and web UI.
    - ~~Integrate these tests into the CI/CD pipeline to run automatically on every commit or pull request. (NA for now)~~
    - Trigger tests on Push to Master branch (customer App/Partner App) and attach resutls as GH Action run (solvable by nightly builds as well)
    - Utilise frameworks and tools suitable for UI testing, ensuring tests simulate real user interactions - uisng Appium cross paltform ios and android testing
- **Test Triggering & Reporting(Nightly builds, thread builds, dev builds, Release candidate builds):**
    - Establish automated triggers (e.g., Flash deployments) to run tests on feature branches and PRs. (Only after complete staging is stable)
    - Integrate slack reporting and dashboards for real-time visibility of test outcomes.
    - Ensure automated test suites cover multiple scenarios including sanity and regression tests. ~~and stress tests~~.
    - Google Ref: https://developer.android.com/training/testing/fundamentals/strategies#minimize-cost
        
        ![image.png](%F0%9F%91%B7Porter%20Test%20Strategy%201%200%201819c6eaaa6d80acb818de834916c790/image.png)
        
    - Google Ref: https://developer.android.com/training/testing/fundamentals/strategies#minimize-cost:~:text=and%20performance%20tests.-,Scope,Lifecycle,-Unit
    
    | **Scope** | **Network access** | **Execution** | **Build type** | **Lifecycle** |
    | --- | --- | --- | --- | --- |
    | **Unit** | Single method or class with minimal dependencies. | No | Local | Debuggable |
    | **Component** | Module or component level
    Multiple classes together | No | Local
    [**Robolectric**](https://developer.android.com/training/testing/local-tests/robolectric)
    **Emulator** | Debuggable |
    | **Feature** | Feature level
    Integration with components owned by other teams | **Mocked** | Local
    [Robolectric](https://developer.android.com/training/testing/local-tests/robolectric)
    Emulator
    **Devices** | Debuggable |
    | **Application** | Application level
    Integration with features and/or services owned by other teams | Mocked
    **Staging server**
    **Prod server** | Emulator
    Devices | Debuggable |
    | **Release Candidate** | Application level
    Integration with features and/or services owned by other teams | Prod server | Emulator
    Devices | **Minified release build** |
- **Consolidated End-to-End Test Suite:**
    - Create an integrated approach that covers API + UI or API + Mobile tests to simulate real-world user journeys.
    - Standardise test case management and reporting through platforms like TestRail, ensuring test cases are the source of truth.
    - Develop clear acceptance criteria for UAT tests and coordinate with teams for regular updates and maintenance.
- **Continuous Feedback & Improvement:**
    - Regularly review and update test cases based on feedback from production deployments.
    - Automate previous 50 production bugs encountered by the thread.
    - Engage SDET teams early in the SDLC to ensure that automated test cases reflect real user scenarios.
    - Perform detailed analysis and reporting of test failures to drive improvements in both the application and the test suite.

---

## Conclusion

By grouping our testing initiatives into these three distinct categories, we aim to create a cohesive test strategy that not only builds confidence in our codebase but also improves developer efficiency and stakeholder trust.

 The focus is on building robust unit tests that serve as the foundation, comprehensive API and cross-service integration tests to validate real dependencies, and thorough mobile/web UI end-to-end tests to ensure the final product meets quality standards in real-world scenarios.

This strategy will be rolled out progressively with clear milestones and measurable key results, ensuring that every stage of the testing process contributes to higher quality, more reliable deployments.

### ~~Breakdown - Do not expand:~~

1. **Current State:**
    1. Unit & Integration Test:
        1. Tools Being Used:
            1. Junit
            2. Spek
        2. Mocking
            1. Mockk
        3. Others
            1. Fixture
            2. Test Container - Being used for Postgres and Redis
            3. Code coverage - SonarCube through Jacoco
            4. Percentage coverage for sample repository??
    2. UAT Tests:
        1. API test (with real deployments, no mocks yet):
            1. Existing OLC E2E from create_trip to complete_trip
                1. Missing wider adoption across repos
            2. Missing API test cases to test business logic from a end user perspective. 
            3. Teams have APIs wiring tests available from GCP migration time and can be utilised to test functionality as well
        2. Mobile App (Android + iOS)
            1. Sanity Suite - 100% automated
            2. Regression Suites - Clarity will be achieved once Test Rail baselines are cleaned
        3. Hybrid Automation tests with both API + Web or API + Mobile App
        4. Tests for CRM and Salesforce are not covered in automation throughly
            1. Partial automation exists as part of mobile app tests but not dedicated CRM or salesforce tests are written yet
2. **Initiatives**
    1. **Unit Testing**
        1. write a test and make it work. [TDD]
        2. test the most granular unit of code 
        3. everything else is mocked, going outside the domain is mocked
        4. mock as a service
        5. no concept of integration testing
        6. data
            1. good data
            2. garbage data
            3. boundary data
    2. **Service Test/APITest/Acceptance Test**
        1. failure testing (FMEA)
            1. any use-case failing like rate limiting
        2. contract testing
        3. observability guidelines
        4. security guidelines
    3. **Performance Testing**
    4. **Local Testing**
        1. Kotlin 
            1. test report is generated by default - aggregated report ?
            2. intelliJ plugin → discovery
            3. test containers
            4. cross service testing → Keploy
        2. Ruby
    5. **TCs Integration with CI**
        1. Reject build, if the test fails
    6. **Test Suite as part of UAT/Production Sanity POV**
        1. Service Testing
        2. Test with all the dependencies - end to end testing
            1. API
            2. UI 
            3. cost of end to end testing
        3. Test Automation Strategy for UAT Tests → E2E API, Web UI & Mobile Application
            
            **Problem Statements:**
            
            1. Test case source of truth cleanup
            2. Automation suite integration to CI/CD pipelines
            3. Test case preparation
            4. SDET involvement early in SDLC is prerequisite 
            5. Functional Test case Review - TL 
                1. Test Script Code Review
                2. Framework code changes review
            6. Automation Test for hotfix/bugfix/config/feature changes
            7. Automation test in Staging Environment
            8. App Release Test strategy
            9. Testing in staging environment with release ready changes from all STTs → Release candidate build
            10. Prod sanity with Release candidate App
        4. Backend Release Test strategy
        5. Test Reporting & Failure analysis
        6. Observability system for automation tests
        
        **High Level Initiatives for UAT - api, web ui & Mobile app:**
        
        1. BaseLine Cleanup doc → [TestRail data cleanup approach document:](https://www.notion.so/TestRail-data-cleanup-approach-document-1579c6eaaa6d803dbd38c30ed6d12b47?pvs=21) 
        2. Automation suite integration to CI/CD pipelines → [Automation Test CI/CD Exploration:](https://www.notion.so/Automation-Test-CI-CD-Exploration-17a9c6eaaa6d804eaf72c8543e2780a4?pvs=21) 
    7. **Quality of TCs**
        1. quality of test cases
        2. audit exclude TCs
        3. SonarQube Analysis
3. **To be State**
    1. Define end to end strategy for taking a change to production
        1. Standardization
            1. guidelines
            2. framework standardization
        2. Policy
            1. linters for auditing the test strategy
        3. Shift left testing
            1. Empowering developers to self test with real dependencies
        4. Testing first before releasing
            1. Currently production is deployed first
            2. Masterbox apps should be deployed first, tested and then released to production

## Discovery, Initiatives, WIP:

[Discovery: Automation process, tools, adoption & other challenges at Porter](https://www.notion.so/Discovery-Automation-process-tools-adoption-other-challenges-at-Porter-1499c6eaaa6d80d7a571c02994e1c54c?pvs=21) 

[TestRail data cleanup approach document:](https://www.notion.so/TestRail-data-cleanup-approach-document-1579c6eaaa6d803dbd38c30ed6d12b47?pvs=21) 

[**Test Strategy as part of SDLC - Discovery**:](https://www.notion.so/Test-Strategy-as-part-of-SDLC-Discovery-1b39c6eaaa6d8037adbac59b602d9ff1?pvs=21) 

[Inservice Testing for Engineers](https://www.notion.so/Inservice-Testing-for-Engineers-1a59c6eaaa6d80a7914cf92beb33c23c?pvs=21) 

[Hogsmeade inservice tests exploration doc:](https://www.notion.so/Hogsmeade-inservice-tests-exploration-doc-1ad9c6eaaa6d80559dd4d1cb699f5e55?pvs=21) 

[🚀 **Increase Code Coverage Locally with JaCoCo**](%F0%9F%91%B7Porter%20Test%20Strategy%201%200%201819c6eaaa6d80acb818de834916c790/%F0%9F%9A%80%20Increase%20Code%20Coverage%20Locally%20with%20JaCoCo%201969c6eaaa6d80c4ab5bfdfab86985c6.md)

[Automation Test CI/CD Exploration:](https://www.notion.so/Automation-Test-CI-CD-Exploration-17a9c6eaaa6d804eaf72c8543e2780a4?pvs=21) 

[**API Test Automation Trigger via Flash:**](https://www.notion.so/API-Test-Automation-Trigger-via-Flash-1bb9c6eaaa6d80dda0f5fe3497b3e43f?pvs=21) 

[🚀 Flash WebHook Capability – Alpha Release](https://www.notion.so/Flash-WebHook-Capability-Alpha-Release-2349c6eaaa6d803a8737fc86670e7194?pvs=21) 

[Guidelines for Jenkins job WebHook  Mapping in Flash for staging](https://www.notion.so/Guidelines-for-Jenkins-job-WebHook-Mapping-in-Flash-for-staging-22c9c6eaaa6d804cbd04d29bbe2fdce7?pvs=21) 

Demo Video: [Link](https://drive.google.com/file/d/1ZKUc_KdvmjWcaMiH5wpPX8FHjxQbEywp/view)

[KRs for Test Strategy](https://www.notion.so/KRs-for-Test-Strategy-1929c6eaaa6d805aa6bbde79f0b82958?pvs=21)

[[POC] Local Jacoco Root Coverage](%F0%9F%91%B7Porter%20Test%20Strategy%201%200%201819c6eaaa6d80acb818de834916c790/%5BPOC%5D%20Local%20Jacoco%20Root%20Coverage%201d79c6eaaa6d8039aea6c14dee351b6b.md)

[Porter Testing Strategy 2.0 Summary:](https://www.notion.so/Porter-Testing-Strategy-2-0-Summary-2269c6eaaa6d803f993fec053681ba02?pvs=21) 

[🏁 Backend Test Strategy 2.0 Roadmap & Milestones](https://www.notion.so/Backend-Test-Strategy-2-0-Roadmap-Milestones-2469c6eaaa6d8068916afaab70e591ce?pvs=21) 

[Contracts For Agentic Support APIs](https://www.notion.so/Contracts-For-Agentic-Support-APIs-21f9c6eaaa6d80f6be40d769f979aa9c?pvs=21)