# 🚀 Increase Code Coverage Locally with JaCoCo

## 🎯 **Objective**

As part of this initiative, we are implementing a process that ensures **developers run code coverage locally**, receive **instant feedback** in their IDE, and **visualize uncovered lines** to improve test coverage **before committing code**.

This will ensure:

✅ **Faster iteration** by catching coverage issues early

✅ **Higher code quality** by identifying untested logic

✅ **Efficient CI/CD usage** by reducing unnecessary GitHub Actions runs

---

## 🛠 **Prerequisites**

🔹 **Colima Setup (For Local Docker Support)**

- If you don’t have **Colima** installed, follow this guide:📌 [Local Docker Setup with Colima](https://www.notion.so/Local-Docker-Setup-with-Colima-2c7d8ae7aca542fa95b3128eec4f2f2c?pvs=21)

🔹 **Gradle-based Kotlin project**

🔹 **IntelliJ IDE Installed**

---

## 📌 **How It Works**

We are introducing two **Gradle Run Configurations** in **IntelliJ**:

1️⃣ **Module-Wise Coverage** → Run tests & generate coverage for a **specific module**

2️⃣ **Root Project Coverage** → Generate a **full aggregated coverage report** for the project

This allows devs to test **locally** instead of waiting for **GitHub Actions** to verify coverage.

---

## 🏗️ **Setup Guide**

### **1️⃣ Add JaCoCo to Your Project**

### **🔹 Step 1: Configure JaCoCo in Your Plugins File**

In your **plugins file**, add:

```kotlin
object Jacoco {
    const val toolVersion = "0.8.9"
    const val jacocoPlugin = "jacoco"
}
```

### **🔹 Step 2: Enable JaCoCo in Gradle**

**In your root `build.gradle.kts` file**, add:

```kotlin
plugins {
    id(OrderSystemPlugins.Jacoco.jacocoPlugin)
}
```

🔗 **Reference:** [Code](https://github.com/porterin/order-system/pull/1802/files#diff-c0dfa6bc7a8685217f70a860145fbdf416d449eaff052fa28352c5cec1a98c06R8)

### **🔹 Step 3: Enable JaCoCo for Subprojects**

Add the following to **subprojects in `build.gradle.kts`**:

```kotlin
subprojects {
    group = "in.porter.ordersystem"
    version = "3.0.47"

    apply(plugin = OrderSystemPlugins.kotlinJvm)
    apply(plugin = OrderSystemPlugins.Jacoco.jacocoPlugin)

    // Configure JaCoCo for subprojects
    jacoco {
        toolVersion = OrderSystemPlugins.Jacoco.toolVersion
    }
}
```

🔗 **Reference:** [Code](https://github.com/porterin/order-system/pull/1802/files#diff-c0dfa6bc7a8685217f70a860145fbdf416d449eaff052fa28352c5cec1a98c06R61)

---

### **2️⃣ Configure the JaCoCo Report Task**

Modify your **Gradle build script** to add jacocoTestReport which ensures Jacoc report triggred after test run.

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // Ensure JaCoCo report is triggered after tests
    environment("INFRA_STACK", "india")
    environment("CONFIG_PATH", "src/test/resources")
}

tasks.withType<JacocoReport> {
    dependsOn("test") // Ensure tests are run before generating the report

    reports {
        xml.required.set(true)  // Required for SonarQube
        html.required.set(true) // Generates a readable HTML report
    }

    sourceDirectories.setFrom(files("src/main/kotlin"))
    classDirectories.setFrom(files("build/classes/kotlin/main"))
}

tasks.withType<JacocoReport>().configureEach {
    outputs.cacheIf { false } // Ensures fresh results every time
}

```

🔗 **Reference PR:** [PR](https://github.com/porterin/order-system/pull/1802/files)

---

## 🛠️ **Setting Up Coverage in IntelliJ**

### **🔹 1. Module-Wise Coverage Configuration (Run Tests per Module)**

1️⃣ **Open IntelliJ** → **Go to** `Run → Edit Configurations`

2️⃣ Click the **➕ (Add New Configuration)** button

3️⃣ Select **Gradle**

4️⃣ Under `Run/Tasks`, enter:

```
test
```

5️⃣ Name it **Build with Coverage**

6️⃣ Under **Gradle Project**, select the **module/submodule** you want to test

7️⃣ Set Environment Variable if not present in system variables:

- **Key**: `TESTCONTAINERS_HOST_OVERRIDE`  **Value**: `192.168.64.3` (localhost)
- **Key**: `TESTCONTAINERS_RYUK_DISABLED` **Value:** `true`

8️⃣ Click **Apply** → **OK**

9️⃣ Run it

[Screen Recording 2025-02-11 at 3 (1).mp4](%F0%9F%9A%80%20Increase%20Code%20Coverage%20Locally%20with%20JaCoCo%201969c6eaaa6d80c4ab5bfdfab86985c6/Screen_Recording_2025-02-11_at_3_(1).mp4)

---

### **🔹 2. Root Coverage Report Configuration (Aggregate Full Project Coverage)**

1️⃣ Ensure the following is in your **root `build.gradle.kts`**:

```kotlin
tasks.register<JacocoReport>("buildWithCoverage") {
    group = "Verification"
    description = "Build project and run tests with coverage."
    dependsOn("test")
}

val jacocoRootReport by tasks.registering(JacocoReport::class) {
    dependsOn(subprojects.map { "${it.path}:test" }) // Run all test tasks before generating coverage

    sourceDirectories.setFrom(files(subprojects.map { it.projectDir.resolve("src/main/kotlin") }))
    classDirectories.setFrom(files(subprojects.mapNotNull {
        it.projectDir.resolve("build/classes/kotlin/main").takeIf { it.exists() }
    }))
    executionData.setFrom(files(subprojects.mapNotNull {
        it.projectDir.resolve("build/jacoco/test.exec").takeIf { it.exists() }
    }))

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

🔗 **Reference PR:** [GitHub PR](https://github.com/porterin/order-system/pull/1802/files)

2️⃣ **Open IntelliJ** → **Go to** `Run → Edit Configurations`

3️⃣ Click the **➕ (Add New Configuration)** button

4️⃣ Select **Gradle**

5️⃣ Under `Run/Tasks`, enter:

```
jacocoRootReport
```

6️⃣ Name it **Build with Root Coverage**

7️⃣ Under **Gradle Project**, select the **Root Module**

8️⃣ Set Environment Variable if not present in system variables:

- **Key**: `TESTCONTAINERS_HOST_OVERRIDE`  **Value**: `192.168.64.3` (localhost)
- **Key**: `TESTCONTAINERS_RYUK_DISABLED` **Value:** `true`

9️⃣ Click **Apply** → **OK**

🔟 Run it

**📌 Reference Video:** [🔗 Link](https://drive.google.com/file/d/1iT_F3GUxTNKgRLsvm_S36p3mJasuwUmp/view)

---

## 🔍 **3️⃣ Viewing Code Coverage in IntelliJ**

1️⃣ **Open IntelliJ** → `Run → Show Coverage Data`

2️⃣ **Remove Old Coverage Data** (if any)

3️⃣ Click the **➕ (Add New Coverage File)**

4️⃣ Add the **coverage XML file path**:

📌 **For Root Coverage:**

```
build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml
```

📌 **For Module-Level Coverage**

```

module/submodule/build/reports/jacoco/test/jacocoTestReport.xml
```

5️⃣ Click **Show Selected**, and you will see:

🟢 **Green** → Covered lines

🟡 **Yellow** → Partially covered lines

🔴 **Red** → Uncovered lines

**📌 Reference Video:** [🔗 Link](https://drive.google.com/file/d/1V6OD1IoOUTabb8ytoICXECXdKIcy7xHV/view?usp=sharing)

---

## 🎯 **Best Practices & Why This Matters**

✅ **Faster Feedback** → Catch missing test coverage **before pushing code**

✅ **Better Developer Experience** → No need to wait for **GitHub Actions** to report low coverage

✅ **Improved Code Quality** → See **uncovered** lines & fix them immediately

✅ **Cost & Time Savings** → Reduces unnecessary **CI/CD runs** & **AWS/Cloud**