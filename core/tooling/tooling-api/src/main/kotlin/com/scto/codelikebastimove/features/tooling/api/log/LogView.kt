package com.scto.codelikebastimove.feature.tooling.api.log

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LogViewProvider {
    val logs: StateFlow<List<LogEntry>>
    val filter: StateFlow<LogFilter>
    val isCapturing: StateFlow<Boolean>
    
    fun startCapture()
    fun stopCapture()
    fun clearLogs()
    
    fun setFilter(filter: LogFilter)
    fun searchLogs(query: String): List<LogEntry>
    
    fun exportLogs(format: LogExportFormat): String
}

data class LogEntry(
    val id: Long,
    val timestamp: Long,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val pid: Int? = null,
    val tid: Int? = null,
    val source: LogSource = LogSource.APP
)

enum class LogLevel(val priority: Int, val shortName: String) {
    VERBOSE(2, "V"),
    DEBUG(3, "D"),
    INFO(4, "I"),
    WARN(5, "W"),
    ERROR(6, "E"),
    ASSERT(7, "A")
}

enum class LogSource {
    APP,
    SYSTEM,
    GRADLE,
    TERMINAL,
    CRASH
}

data class LogFilter(
    val minLevel: LogLevel = LogLevel.VERBOSE,
    val tags: Set<String> = emptySet(),
    val sources: Set<LogSource> = LogSource.entries.toSet(),
    val searchQuery: String? = null,
    val showPid: Boolean = true,
    val showTimestamp: Boolean = true
)

enum class LogExportFormat {
    PLAIN_TEXT,
    JSON,
    CSV,
    HTML
}

interface LogParser {
    fun parseLine(line: String): LogEntry?
    fun parseLogcat(output: String): List<LogEntry>
}

interface LogcatProvider {
    val logcatOutput: Flow<LogEntry>
    
    suspend fun startLogcat(filter: LogFilter): Result<Unit>
    suspend fun stopLogcat(): Result<Unit>
    suspend fun clearLogcat(): Result<Unit>
}
