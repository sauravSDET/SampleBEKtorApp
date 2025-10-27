# [POC] Local Jacoco Root Coverage

Approches:

1. Get the root coverage of the executable files
2. Get coverage by combining the JacocoReport files

You should have this for both approaches

```kotlin
tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        sourceDirectories.setFrom(files("src/main/kotlin"))
        classDirectories.setFrom(files("build/classes/kotlin/main"))
    }
```

- Get the root coverage of the executable files
    
    ```kotlin
    val jacocoRootReport by tasks.registering(JacocoReport::class) {
        dependsOn(subprojects.map { "${it.path}:jacocoTestReport" })
    
        classDirectories.setFrom(
            files(
                subprojects.mapNotNull { subproject ->
                    subproject.projectDir.resolve("build/classes/kotlin/main").takeIf { it.exists() }?.let { mainDir ->
                        fileTree(mainDir) {
                            exclude(
                                "**/di/**",
                                "**/deploy/chart/**",
                                "**/build.gradle.kts",
                                "**/MainRunner.kt",
                                "**/MainApplication.kt",
                                "**/AsyncEventWorkAction.kt",
                                "**/GetStatusHttpService.kt",
                                " **/JobPubSubAction.kt",
                                "**/datascience/**",
                                "**/driverfeatures/**",
                                "**/FareValue.kt",
                            )
                        }
                    }
                }
            )
        )
    
        sourceDirectories.setFrom(
            files(subprojects.map { it.projectDir.resolve("src/main/kotlin") })
        )
    
        executionData.setFrom(
            files(subprojects.mapNotNull {
                it.projectDir.resolve("build/jacoco/test.exec").takeIf { exec -> exec.exists() }
            })
        )
    
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    
        doLast {
            val reportFile = buildDir.resolve("reports/jacoco/jacocoRootReport/jacocoRootReport.xml")
    
        val coverage = parseJacocoReport(reportFile)
        println("✅ **Final Coverage: ${"%.2f".format(coverage)}%**")
        }
    }
    
    fun parseJacocoReport(reportFile: File): Double {
        if (!reportFile.exists()) {
            println("⚠️ JaCoCo XML report not found at: ${reportFile.absolutePath}")
            return 0.0
        }
    
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isValidating = false
            factory.isNamespaceAware = false
    
            // Prevent external entity attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    
            val builder = factory.newDocumentBuilder()
            builder.setEntityResolver { _, _ -> InputSource(StringReader("")) }
    
            val document: Document = builder.parse(reportFile)
            val xPath = XPathFactory.newInstance().newXPath()
    
            // Extracting metrics
            val totalLines = xPath.evaluate("sum(//counter[@type='LINE']/@missed) + sum(//counter[@type='LINE']/@covered)", document).toDoubleOrNull() ?: 0.0
            val coveredLines = xPath.evaluate("sum(//counter[@type='LINE']/@covered)", document).toDoubleOrNull() ?: 0.0
            val totalConditions = xPath.evaluate("sum(//counter[@type='BRANCH']/@missed) + sum(//counter[@type='BRANCH']/@covered)", document).toDoubleOrNull() ?: 0.0
            val uncoveredConditions = xPath.evaluate("sum(//counter[@type='BRANCH']/@missed)", document).toDoubleOrNull() ?: 0.0
    
            println("totalLines $totalLines")
            println("coveredLines $coveredLines")
            println("totalConditions $totalConditions")
            println("uncoveredConditions $uncoveredConditions")
    
            // SonarQube Calculation
            val CT = totalConditions - uncoveredConditions // Conditions evaluated to true at least once
            val LC = coveredLines // Covered lines
            val B = totalConditions // Total conditions
            val EL = totalLines // Executable lines
    
            val coverage = if (B + EL > 0) ((CT + LC) / (B + EL)) * 100 else 0.0
    
            println("✅ **SonarQube Code Coverage (Based on JaCoCo Report): ${"%.2f".format(coverage)}%**")
            coverage
        } catch (e: Exception) {
            println("❌ Error parsing JaCoCo report: ${e.message}")
            0.0
        }
    }
    ```
    
- Get coverage by combining the JacocoReport files
    
    ```kotlin
    val jacocoRootReportNew1 by tasks.registering {
        dependsOn(subprojects.map { "${it.path}:jacocoTestReport" })
    
        doLast {
            val moduleReports = subprojects.mapNotNull { subproject ->
                val reportFile = subproject.buildDir.resolve("reports/jacoco/test/jacocoTestReport.xml")
                if (reportFile.exists()) reportFile else null
            }
    
            if (moduleReports.isEmpty()) {
                println("⚠️ No JaCoCo reports found! Ensure tests are running.")
                return@doLast
            }
    
            var totalLines = 0.0
            var uncoveredLines = 0.0
            var totalConditions = 0.0
            var uncoveredConditions = 0.0
    
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val rootDocument = documentBuilder.newDocument()
            val rootElement = rootDocument.createElement("report")
            rootDocument.appendChild(rootElement)
    
            // Process each module report
            moduleReports.forEach { reportFile ->
                val (lines, uncovered, conditions, uncoveredCond, moduleDocument) = parseJacocoReportForMerging(reportFile)
    
                totalLines += lines
                uncoveredLines += uncovered
                totalConditions += conditions
                uncoveredConditions += uncoveredCond
    
                // Merge module data into root XML
                moduleDocument?.documentElement?.let { moduleRoot ->
                    val importedNode = rootDocument.importNode(moduleRoot, true)
                    rootElement.appendChild(importedNode)
                }
            }
    
            // Compute final aggregated coverage
            val CT = totalConditions - uncoveredConditions // Conditions evaluated to true at least once
            val LC = totalLines - uncoveredLines // Covered lines
            val B = totalConditions // Total conditions
            val EL = totalLines // Executable lines
    
            val finalCoverage = if (B + EL > 0) ((CT + LC) / (B + EL)) * 100 else 0.0
            val finalCoverageRounded = "%.2f".format(finalCoverage).toDouble()
    
            println("📊 **Final Aggregated JaCoCo Report (Merged from Modules)**")
            println("🔹 Lines to Cover: $totalLines")
            println("🔹 Uncovered Lines: $uncoveredLines")
            println("🔹 Conditions to Cover: $totalConditions")
            println("🔹 Uncovered Conditions: $uncoveredConditions")
            println("✅ **Final Aggregated Coverage: $finalCoverageRounded%**")
    
            // Save the merged report as `jacocoRootReport.xml`
            val reportFile = File(buildDir, "reports/jacoco/jacocoRootReport/jacocoRootReport.xml")
            reportFile.parentFile.mkdirs()
            saveXmlDocument(rootDocument, reportFile)
            println("✅ Merged JaCoCo report saved: ${reportFile.absolutePath}")
        }
    }
    
    fun parseJacocoReportForMerging(reportFile: File): Quintuple<Double, Double, Double, Double, Document?> {
        return try {
            val factory = DocumentBuilderFactory.newInstance().apply {
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                setFeature("http://xml.org/sax/features/external-general-entities", false)
                setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            }
    
            val builder = factory.newDocumentBuilder().apply {
                setEntityResolver { _, _ -> InputSource(StringReader("")) } // Prevent DTD loading
            }
    
            val document: Document = builder.parse(reportFile)
            val xPath = XPathFactory.newInstance().newXPath()
    
            // ✅ Corrected XPath to ONLY ignore annotation-related lines but keep class & methods
            val exclusionCriteria = """
                not(
                    ancestor::counter[@type='LINE' and ancestor::method/annotation[contains(@desc, 'Ljavax/inject/Inject;')]] or
                    ancestor::counter[@type='LINE' and ancestor::method/annotation[contains(@desc, 'Ljavax/inject/Named;')]]
                )
            """.trimIndent()
    
            val totalLines = xPath.evaluate(
                "sum(//counter[@type='LINE' and $exclusionCriteria]/@missed) + " +
                        "sum(//counter[@type='LINE' and $exclusionCriteria]/@covered)", document
            ).toDoubleOrNull() ?: 0.0
    
            val uncoveredLines = xPath.evaluate(
                "sum(//counter[@type='LINE' and $exclusionCriteria]/@missed)", document
            ).toDoubleOrNull() ?: 0.0
    
            val totalConditions = xPath.evaluate(
                "sum(//counter[@type='BRANCH' and $exclusionCriteria]/@missed) + " +
                        "sum(//counter[@type='BRANCH' and $exclusionCriteria]/@covered)", document
            ).toDoubleOrNull() ?: 0.0
    
            val uncoveredConditions = xPath.evaluate(
                "sum(//counter[@type='BRANCH' and $exclusionCriteria]/@missed)", document
            ).toDoubleOrNull() ?: 0.0
    
            println("📊 Module Report (Ignoring DI Annotations Only): ${reportFile.name}")
            println("   ➝ Lines to Cover: $totalLines")
            println("   ➝ Uncovered Lines: $uncoveredLines")
            println("   ➝ Conditions to Cover: $totalConditions")
            println("   ➝ Uncovered Conditions: $uncoveredConditions")
    
            Quintuple(totalLines, uncoveredLines, totalConditions, uncoveredConditions, document)
        } catch (e: Exception) {
            println("❌ Error parsing JaCoCo report: ${e.message}")
            Quintuple(0.0, 0.0, 0.0, 0.0, null)
        }
    }
    
    // Function to save an XML document to file
    fun saveXmlDocument(document: Document, file: File) {
        try {
            val transformerFactory: TransformerFactory = TransformerFactory.newInstance()
            val transformer: Transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            val source = DOMSource(document)
            val result = StreamResult(file)
            transformer.transform(source, result)
        } catch (e: Exception) {
            println("❌ Error saving merged JaCoCo report: ${e.message}")
        }
    }
    
    // Helper class to return five values
    data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
    
    ```
    
- Other approaches
    
    ```kotlin
    val jacocoRootReportNew by tasks.registering {
        dependsOn(subprojects.map { "${it.path}:jacocoTestReport" })
    
        doLast {
            val moduleReports = subprojects.mapNotNull { subproject ->
                val reportFile = subproject.buildDir.resolve("reports/jacoco/test/jacocoTestReport.xml")
                if (reportFile.exists()) reportFile else null
            }
    
            if (moduleReports.isEmpty()) {
                println("⚠️ No JaCoCo reports found! Ensure tests are running.")
                return@doLast
            }
    
            var totalLines = 0.0
            var coveredLines = 0.0
            var totalConditions = 0.0
            var uncoveredConditions = 0.0
    
            // Process each module report
            moduleReports.forEach { reportFile ->
                val (lines, covered, conditions, uncoveredCond) = parseJacocoReportNew(reportFile)
                totalLines += lines
                coveredLines += covered
                totalConditions += conditions
                uncoveredConditions += uncoveredCond
            }
    
            // Compute final aggregated coverage
            val CT = totalConditions - uncoveredConditions // Conditions evaluated to true at least once
            val LC = coveredLines // Covered lines
            val B = totalConditions // Total conditions
            val EL = totalLines // Executable lines
    
            println("Lines to cover: $totalLines")
            println("Uncovered Lines: ${totalLines - coveredLines}")
            println("Condition to Cover: $totalConditions")
            println("Uncovered Conditions: $uncoveredConditions")
    
            val finalCoverage = if (B + EL > 0) ((CT + LC) / (B + EL)) * 100 else 0.0
    
            println("✅ **Final Aggregated Coverage: ${"%.2f".format(finalCoverage)}%**")
        }
    }
    
    // Function to extract line coverage from JaCoCo XML
    fun parseJacocoReportNew(reportFile: File): Quadruple<Double, Double, Double, Double> {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isValidating = false
            factory.isNamespaceAware = false
    
            // Disable DTD loading to prevent `report.dtd` error
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    
            val builder = factory.newDocumentBuilder()
            builder.setEntityResolver { _, _ -> InputSource(StringReader("")) } // Ignore external entities
    
            val document: Document = builder.parse(reportFile)
            val xPath = XPathFactory.newInstance().newXPath()
    
            val totalLines = xPath.evaluate("sum(//counter[@type='LINE']/@missed) + sum(//counter[@type='LINE']/@covered)", document).toDoubleOrNull() ?: 0.0
            val coveredLines = xPath.evaluate("sum(//counter[@type='LINE']/@covered)", document).toDoubleOrNull() ?: 0.0
            val totalConditions = xPath.evaluate("sum(//counter[@type='BRANCH']/@missed) + sum(//counter[@type='BRANCH']/@covered)", document).toDoubleOrNull() ?: 0.0
            val uncoveredConditions = xPath.evaluate("sum(//counter[@type='BRANCH']/@missed)", document).toDoubleOrNull() ?: 0.0
    
            println("📊 Processing JaCoCo Report: ${reportFile.name}")
            println("Module name: ${reportFile.parentFile.name}")
            println("file path: ${reportFile.absolutePath}")
            println("   ➝ Total Lines: $totalLines")
            println("   ➝ Covered Lines: $coveredLines")
            println("   ➝ Total Conditions: $totalConditions")
            println("   ➝ Uncovered Conditions: $uncoveredConditions")
    
            return Quadruple(totalLines, coveredLines, totalConditions, uncoveredConditions)
        } catch (e: Exception) {
            println("❌ Error parsing JaCoCo report: ${e.message}")
            return Quadruple(0.0, 0.0, 0.0, 0.0)
        }
    }
    
    // Helper class to return four values
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
    
    ```
    

`We can try this and send the root report to SonarQube and verify that both coverages are matching.`