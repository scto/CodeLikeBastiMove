package com.scto.codelikebastimove.feature.tooling.api.bridge

import com.scto.codelikebastimove.feature.tooling.api.gradle.GradleBuildResult
import com.scto.codelikebastimove.feature.tooling.api.gradle.GradleOutputProvider
import com.scto.codelikebastimove.feature.tooling.api.log.LogViewProvider
import com.scto.codelikebastimove.feature.tooling.api.terminal.TerminalBridge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ToolingBridge {
    val gradleProvider: GradleOutputProvider
    val logProvider: LogViewProvider
    val terminalProvider: TerminalBridge
    
    val activeTool: StateFlow<ActiveTool>
    
    suspend fun initialize(): Result<Unit>
    suspend fun shutdown(): Result<Unit>
    
    fun switchTool(tool: ActiveTool)
    
    suspend fun runGradleTask(taskPath: String): Flow<GradleBuildResult>
    suspend fun runGradleTasks(taskPaths: List<String>): Flow<GradleBuildResult>
    
    suspend fun executeInTerminal(command: String): Result<Unit>
    
    fun getUnifiedOutput(): Flow<UnifiedOutputLine>
}

enum class ActiveTool {
    TERMINAL,
    BUILD_OUTPUT,
    LOGCAT,
    PROBLEMS,
    TODO
}

data class UnifiedOutputLine(
    val content: String,
    val source: OutputSource,
    val level: OutputLevel,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OutputSource {
    GRADLE,
    TERMINAL,
    LOGCAT,
    SYSTEM
}

enum class OutputLevel {
    INFO,
    DEBUG,
    WARNING,
    ERROR
}

interface ToolingBridgeFactory {
    fun create(config: ToolingConfig): ToolingBridge
}

data class ToolingConfig(
    val projectPath: String,
    val gradleWrapper: String = "gradlew",
    val javaHome: String? = null,
    val gradleUserHome: String? = null,
    val terminalConfig: TerminalConfig = TerminalConfig(),
    val logConfig: LogConfig = LogConfig()
)

data class TerminalConfig(
    val shell: String = "/system/bin/sh",
    val initialDirectory: String = "/",
    val environment: Map<String, String> = emptyMap()
)

data class LogConfig(
    val bufferSize: Int = 10000,
    val autoScroll: Boolean = true,
    val wrapLines: Boolean = false
)

interface BuildOutputParser {
    fun parseErrorLine(line: String): ParsedError?
    fun parseWarningLine(line: String): ParsedWarning?
    fun parseTaskLine(line: String): ParsedTask?
}

data class ParsedError(
    val file: String,
    val line: Int,
    val column: Int?,
    val message: String,
    val type: ErrorType
)

data class ParsedWarning(
    val file: String?,
    val line: Int?,
    val message: String
)

data class ParsedTask(
    val path: String,
    val status: TaskStatus,
    val duration: Long?
)

enum class ErrorType {
    COMPILATION,
    LINT,
    RESOURCE,
    MANIFEST,
    DEPENDENCY,
    CONFIGURATION,
    RUNTIME,
    UNKNOWN
}

enum class TaskStatus {
    STARTING,
    UP_TO_DATE,
    FROM_CACHE,
    EXECUTED,
    SKIPPED,
    FAILED,
    NO_SOURCE
}
