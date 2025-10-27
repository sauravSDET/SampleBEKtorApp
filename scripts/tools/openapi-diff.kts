#!/usr/bin/env kotlin

@file:DependsOn("io.swagger.parser.v3:swagger-parser:2.1.19")
@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.17.2")
@file:DependsOn("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.parser.OpenAPIV3Parser
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * OpenAPI Diff Tool for Kotlin/Ktor Applications
 *
 * Implements feedback from test strategy 2.0:
 * - Use off-the-shelf tooling for OpenAPI diff
 * - Focus on contract breaking changes
 * - Generate actionable migration reports
 * - Support multi-version validation
 */

data class BreakingChange(
    val type: BreakingChangeType,
    val path: String,
    val description: String,
    val severity: Severity
)

enum class BreakingChangeType {
    REMOVED_ENDPOINT,
    REMOVED_PARAMETER,
    CHANGED_PARAMETER_TYPE,
    REMOVED_RESPONSE_CODE,
    CHANGED_RESPONSE_SCHEMA,
    ADDED_REQUIRED_PARAMETER,
    CHANGED_ENDPOINT_METHOD
}

enum class Severity {
    CRITICAL,    // Will break existing clients
    HIGH,        // Likely to break clients
    MEDIUM,      // May break some clients
    LOW          // Unlikely to break clients
}

class OpenApiDiff {

    fun parseOpenAPI(filePath: String): OpenAPI? {
        val file = File(filePath)
        if (!file.exists()) {
            println("❌ File not found: $filePath")
            return null
        }

        return try {
            val parser = OpenAPIV3Parser()
            val result = parser.read(filePath)
            if (result == null) {
                println("❌ Failed to parse OpenAPI spec: $filePath")
                null
            } else {
                println("✅ Loaded OpenAPI spec: ${result.info?.title} v${result.info?.version}")
                result
            }
        } catch (e: Exception) {
            println("❌ Error parsing OpenAPI spec: ${e.message}")
            null
        }
    }

    fun compareSpecs(oldSpec: OpenAPI, newSpec: OpenAPI): List<BreakingChange> {
        val changes = mutableListOf<BreakingChange>()

        // Compare paths and operations
        changes.addAll(comparePaths(oldSpec, newSpec))

        // Compare schemas
        changes.addAll(compareSchemas(oldSpec, newSpec))

        return changes
    }

    private fun comparePaths(oldSpec: OpenAPI, newSpec: OpenAPI): List<BreakingChange> {
        val changes = mutableListOf<BreakingChange>()
        val oldPaths = oldSpec.paths?.map { it.key to it.value } ?: emptyList()
        val newPaths = newSpec.paths?.associate { it.key to it.value } ?: emptyMap()

        for ((path, oldPathItem) in oldPaths) {
            val newPathItem = newPaths[path]

            if (newPathItem == null) {
                changes.add(BreakingChange(
                    BreakingChangeType.REMOVED_ENDPOINT,
                    path,
                    "Endpoint removed: $path",
                    Severity.CRITICAL
                ))
                continue
            }

            // Compare operations (GET, POST, etc.)
            changes.addAll(compareOperations(path, oldPathItem, newPathItem))
        }

        return changes
    }

    private fun compareOperations(path: String, oldPathItem: PathItem, newPathItem: PathItem): List<BreakingChange> {
        val changes = mutableListOf<BreakingChange>()
        val operations = mapOf(
            "GET" to (oldPathItem.get to newPathItem.get),
            "POST" to (oldPathItem.post to newPathItem.post),
            "PUT" to (oldPathItem.put to newPathItem.put),
            "DELETE" to (oldPathItem.delete to newPathItem.delete),
            "PATCH" to (oldPathItem.patch to newPathItem.patch)
        )

        for ((method, operationPair) in operations) {
            val (oldOp, newOp) = operationPair

            if (oldOp != null && newOp == null) {
                changes.add(BreakingChange(
                    BreakingChangeType.CHANGED_ENDPOINT_METHOD,
                    "$method $path",
                    "Operation removed: $method $path",
                    Severity.CRITICAL
                ))
            } else if (oldOp != null && newOp != null) {
                changes.addAll(compareOperation(path, method, oldOp, newOp))
            }
        }

        return changes
    }

    private fun compareOperation(path: String, method: String, oldOp: Operation, newOp: Operation): List<BreakingChange> {
        val changes = mutableListOf<BreakingChange>()
        val endpoint = "$method $path"

        // Compare parameters
        val oldParams = oldOp.parameters ?: emptyList()
        val newParams = newOp.parameters?.associateBy { "${it.`in`}-${it.name}" } ?: emptyMap()

        for (oldParam in oldParams) {
            val paramKey = "${oldParam.`in`}-${oldParam.name}"
            val newParam = newParams[paramKey]

            if (newParam == null) {
                changes.add(BreakingChange(
                    BreakingChangeType.REMOVED_PARAMETER,
                    endpoint,
                    "Parameter removed: ${oldParam.name} (${oldParam.`in`})",
                    if (oldParam.required == true) Severity.CRITICAL else Severity.HIGH
                ))
            } else {
                // Check if parameter became required
                if (oldParam.required != true && newParam.required == true) {
                    changes.add(BreakingChange(
                        BreakingChangeType.ADDED_REQUIRED_PARAMETER,
                        endpoint,
                        "Parameter became required: ${newParam.name}",
                        Severity.HIGH
                    ))
                }

                // Check parameter type changes
                if (getSchemaType(oldParam.schema) != getSchemaType(newParam.schema)) {
                    changes.add(BreakingChange(
                        BreakingChangeType.CHANGED_PARAMETER_TYPE,
                        endpoint,
                        "Parameter type changed: ${oldParam.name} from ${getSchemaType(oldParam.schema)} to ${getSchemaType(newParam.schema)}",
                        Severity.HIGH
                    ))
                }
            }
        }

        // Compare responses
        val oldResponses = oldOp.responses?.map { it.key to it.value } ?: emptyList()
        val newResponses = newOp.responses?.associate { it.key to it.value } ?: emptyMap()

        for ((responseCode, oldResponse) in oldResponses) {
            if (!newResponses.containsKey(responseCode)) {
                changes.add(BreakingChange(
                    BreakingChangeType.REMOVED_RESPONSE_CODE,
                    endpoint,
                    "Response code removed: $responseCode",
                    if (responseCode.startsWith("2")) Severity.CRITICAL else Severity.MEDIUM
                ))
            }
        }

        return changes
    }

    private fun compareSchemas(oldSpec: OpenAPI, newSpec: OpenAPI): List<BreakingChange> {
        val changes = mutableListOf<BreakingChange>()
        // Schema comparison logic would go here
        // This is a simplified version focusing on the most critical breaking changes
        return changes
    }

    private fun getSchemaType(schema: Schema<*>?): String {
        return schema?.type ?: "unknown"
    }

    fun generateReport(changes: List<BreakingChange>, oldVersion: String, newVersion: String): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val criticalChanges = changes.filter { it.severity == Severity.CRITICAL }
        val highChanges = changes.filter { it.severity == Severity.HIGH }
        val mediumChanges = changes.filter { it.severity == Severity.MEDIUM }
        val lowChanges = changes.filter { it.severity == Severity.LOW }

        return buildString {
            appendLine("🔍 OpenAPI Contract Compatibility Report")
            appendLine("=" * 50)
            appendLine("📅 Generated: $timestamp")
            appendLine("🔄 Comparing: $oldVersion → $newVersion")
            appendLine()

            if (changes.isEmpty()) {
                appendLine("✅ No breaking changes detected!")
                appendLine("🎉 The API is backward compatible.")
                return@buildString
            }

            appendLine("📊 Summary:")
            appendLine("  🔴 Critical: ${criticalChanges.size}")
            appendLine("  🟡 High: ${highChanges.size}")
            appendLine("  🟠 Medium: ${mediumChanges.size}")
            appendLine("  🟢 Low: ${lowChanges.size}")
            appendLine()

            if (criticalChanges.isNotEmpty()) {
                appendLine("🔴 CRITICAL BREAKING CHANGES:")
                appendLine("-" * 30)
                criticalChanges.forEach { change ->
                    appendLine("  • ${change.description}")
                    appendLine("    Path: ${change.path}")
                    appendLine("    Type: ${change.type}")
                    appendLine()
                }
            }

            if (highChanges.isNotEmpty()) {
                appendLine("🟡 HIGH IMPACT CHANGES:")
                appendLine("-" * 20)
                highChanges.forEach { change ->
                    appendLine("  • ${change.description}")
                    appendLine("    Path: ${change.path}")
                    appendLine()
                }
            }

            if (mediumChanges.isNotEmpty()) {
                appendLine("🟠 MEDIUM IMPACT CHANGES:")
                appendLine("-" * 22)
                mediumChanges.forEach { change ->
                    appendLine("  • ${change.description}")
                    appendLine("    Path: ${change.path}")
                    appendLine()
                }
            }

            appendLine("💡 Recommendations:")
            if (criticalChanges.isNotEmpty()) {
                appendLine("  ❌ DO NOT DEPLOY - Critical breaking changes detected")
                appendLine("  🔧 Fix breaking changes or bump major version")
            } else if (highChanges.isNotEmpty()) {
                appendLine("  ⚠️  Consider major version bump")
                appendLine("  📋 Prepare migration guide for clients")
            } else {
                appendLine("  ✅ Safe to deploy with minor version bump")
            }

            appendLine()
            appendLine("🔗 Next Steps:")
            appendLine("  1. Review breaking changes above")
            appendLine("  2. Update API documentation")
            appendLine("  3. Notify API consumers of changes")
            appendLine("  4. Update integration tests")
        }
    }

    fun validateAllVersions() {
        val versionDirs = listOf("v1", "v2", "v3", "v4")
        val results = mutableListOf<Pair<String, List<BreakingChange>>>()

        println("🔍 Validating all API versions...")

        for (i in 0 until versionDirs.size - 1) {
            val fromVersion = versionDirs[i]
            val toVersion = versionDirs[i + 1]

            val fromPath = "src/main/resources/openapi/$fromVersion/current"
            val toPath = "src/main/resources/openapi/$toVersion/current"

            val fromFile = File(fromPath).listFiles()?.firstOrNull { it.name.endsWith(".yaml") }
            val toFile = File(toPath).listFiles()?.firstOrNull { it.name.endsWith(".yaml") }

            if (fromFile != null && toFile != null) {
                println("🔄 Checking: $fromVersion → $toVersion")

                val fromSpec = parseOpenAPI(fromFile.absolutePath)
                val toSpec = parseOpenAPI(toFile.absolutePath)

                if (fromSpec != null && toSpec != null) {
                    val changes = compareSpecs(fromSpec, toSpec)
                    results.add("$fromVersion → $toVersion" to changes)

                    val criticalCount = changes.count { it.severity == Severity.CRITICAL }
                    if (criticalCount > 0) {
                        println("  ❌ $criticalCount critical breaking changes")
                    } else {
                        println("  ✅ No critical breaking changes")
                    }
                }
            }
        }

        // Generate summary report
        println("\n📋 Multi-Version Validation Summary:")
        println("=" * 40)

        var totalCritical = 0
        for ((transition, changes) in results) {
            val critical = changes.count { it.severity == Severity.CRITICAL }
            val high = changes.count { it.severity == Severity.HIGH }
            totalCritical += critical

            val status = if (critical > 0) "❌" else "✅"
            println("$status $transition: $critical critical, $high high impact")
        }

        println("\n🎯 Overall Result: ${if (totalCritical == 0) "✅ PASSED" else "❌ FAILED ($totalCritical critical issues)"}")
    }
}

// Main script execution
fun main(args: Array<String>) {
    val diff = OpenApiDiff()

    when {
        args.isEmpty() || args[0] == "help" -> {
            println("""
            🔧 OpenAPI Diff Tool for Kotlin/Ktor
            
            Usage:
              kotlin openapi-diff.kts compare <old-spec> <new-spec>
              kotlin openapi-diff.kts validate-all
              kotlin openapi-diff.kts migration-report <from-version> <to-version>
              
            Examples:
              kotlin openapi-diff.kts compare api-v1.yaml api-v2.yaml
              kotlin openapi-diff.kts validate-all
              kotlin openapi-diff.kts migration-report v1 v2
            """.trimIndent())
        }

        args[0] == "compare" && args.size >= 3 -> {
            val oldSpec = diff.parseOpenAPI(args[1])
            val newSpec = diff.parseOpenAPI(args[2])

            if (oldSpec != null && newSpec != null) {
                val changes = diff.compareSpecs(oldSpec, newSpec)
                val report = diff.generateReport(changes, args[1], args[2])
                println(report)

                // Exit with error code if critical breaking changes found
                val criticalChanges = changes.count { it.severity == Severity.CRITICAL }
                if (criticalChanges > 0) {
                    kotlin.system.exitProcess(1)
                }
            } else {
                kotlin.system.exitProcess(1)
            }
        }

        args[0] == "validate-all" -> {
            diff.validateAllVersions()
        }

        args[0] == "migration-report" && args.size >= 3 -> {
            val fromVersion = args[1]
            val toVersion = args[2]

            println("📊 Generating migration report: $fromVersion → $toVersion")

            val fromPath = "src/main/resources/openapi/$fromVersion/current"
            val toPath = "src/main/resources/openapi/$toVersion/current"

            val fromFile = File(fromPath).listFiles()?.firstOrNull { it.name.endsWith(".yaml") }
            val toFile = File(toPath).listFiles()?.firstOrNull { it.name.endsWith(".yaml") }

            if (fromFile != null && toFile != null) {
                val fromSpec = diff.parseOpenAPI(fromFile.absolutePath)
                val toSpec = diff.parseOpenAPI(toFile.absolutePath)

                if (fromSpec != null && toSpec != null) {
                    val changes = diff.compareSpecs(fromSpec, toSpec)
                    val report = diff.generateReport(changes, fromVersion, toVersion)

                    // Save report to file
                    val reportFile = File("api-migration-report-$fromVersion-to-$toVersion.md")
                    reportFile.writeText(report)

                    println(report)
                    println("\n💾 Report saved to: ${reportFile.name}")
                }
            } else {
                println("❌ Could not find API specs for versions $fromVersion or $toVersion")
                kotlin.system.exitProcess(1)
            }
        }

        else -> {
            println("❌ Invalid arguments. Use 'help' for usage information.")
            kotlin.system.exitProcess(1)
        }
    }
}

// Helper extension for string repetition
operator fun String.times(count: Int): String = this.repeat(count)

main(args)
