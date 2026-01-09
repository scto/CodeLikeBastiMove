package com.scto.codelikebastimove.features.tooling.impl.log

import com.scto.codelikebastimove.features.tooling.api.bridge.LogConfig
import com.scto.codelikebastimove.features.tooling.api.log.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class DefaultLogViewProvider(
    private val config: LogConfig
) : LogViewProvider {
    
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    override val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()
    
    private val _filter = MutableStateFlow(LogFilter())
    override val filter: StateFlow<LogFilter> = _filter.asStateFlow()
    
    private val _isCapturing = MutableStateFlow(false)
    override val isCapturing: StateFlow<Boolean> = _isCapturing.asStateFlow()
    
    private var entryIdCounter = 0L
    private val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
    
    override fun startCapture() {
        _isCapturing.value = true
    }
    
    override fun stopCapture() {
        _isCapturing.value = false
    }
    
    override fun clearLogs() {
        _logs.value = emptyList()
        entryIdCounter = 0L
    }
    
    override fun setFilter(filter: LogFilter) {
        _filter.value = filter
    }
    
    override fun searchLogs(query: String): List<LogEntry> {
        return _logs.value.filter { entry ->
            entry.message.contains(query, ignoreCase = true) ||
            entry.tag.contains(query, ignoreCase = true)
        }
    }
    
    override fun exportLogs(format: LogExportFormat): String {
        val filteredLogs = applyFilter(_logs.value)
        
        return when (format) {
            LogExportFormat.PLAIN_TEXT -> exportAsPlainText(filteredLogs)
            LogExportFormat.JSON -> exportAsJson(filteredLogs)
            LogExportFormat.CSV -> exportAsCsv(filteredLogs)
            LogExportFormat.HTML -> exportAsHtml(filteredLogs)
        }
    }
    
    fun appendLog(entry: LogEntry) {
        if (!_isCapturing.value) return
        
        val newEntry = entry.copy(id = ++entryIdCounter)
        
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(newEntry)
        
        if (currentLogs.size > config.bufferSize) {
            currentLogs.removeAt(0)
        }
        
        _logs.value = currentLogs
    }
    
    fun appendLog(level: LogLevel, tag: String, message: String, source: LogSource = LogSource.APP) {
        appendLog(LogEntry(
            id = 0,
            timestamp = System.currentTimeMillis(),
            level = level,
            tag = tag,
            message = message,
            source = source
        ))
    }
    
    private fun applyFilter(logs: List<LogEntry>): List<LogEntry> {
        val currentFilter = _filter.value
        
        return logs.filter { entry ->
            entry.level.priority >= currentFilter.minLevel.priority &&
            (currentFilter.tags.isEmpty() || currentFilter.tags.contains(entry.tag)) &&
            currentFilter.sources.contains(entry.source) &&
            (currentFilter.searchQuery.isNullOrEmpty() || 
             entry.message.contains(currentFilter.searchQuery, ignoreCase = true) ||
             entry.tag.contains(currentFilter.searchQuery, ignoreCase = true))
        }
    }
    
    private fun exportAsPlainText(logs: List<LogEntry>): String {
        return logs.joinToString("\n") { entry ->
            val timestamp = dateFormat.format(Date(entry.timestamp))
            val pid = entry.pid?.let { " $it" } ?: ""
            "$timestamp$pid ${entry.level.shortName}/${entry.tag}: ${entry.message}"
        }
    }
    
    private fun exportAsJson(logs: List<LogEntry>): String {
        val jsonEntries = logs.map { entry ->
            """{"timestamp":${entry.timestamp},"level":"${entry.level.name}","tag":"${escapeJson(entry.tag)}","message":"${escapeJson(entry.message)}","source":"${entry.source.name}"}"""
        }
        return "[${jsonEntries.joinToString(",")}]"
    }
    
    private fun exportAsCsv(logs: List<LogEntry>): String {
        val header = "Timestamp,Level,Tag,Message,Source,PID"
        val rows = logs.map { entry ->
            val timestamp = dateFormat.format(Date(entry.timestamp))
            "\"$timestamp\",\"${entry.level.name}\",\"${escapeCsv(entry.tag)}\",\"${escapeCsv(entry.message)}\",\"${entry.source.name}\",\"${entry.pid ?: ""}\""
        }
        return (listOf(header) + rows).joinToString("\n")
    }
    
    private fun exportAsHtml(logs: List<LogEntry>): String {
        val rows = logs.map { entry ->
            val timestamp = dateFormat.format(Date(entry.timestamp))
            val levelClass = entry.level.name.lowercase()
            "<tr class=\"$levelClass\"><td>$timestamp</td><td>${entry.level.shortName}</td><td>${escapeHtml(entry.tag)}</td><td>${escapeHtml(entry.message)}</td></tr>"
        }
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    table { border-collapse: collapse; width: 100%; font-family: monospace; font-size: 12px; }
                    th, td { border: 1px solid #ddd; padding: 4px; text-align: left; }
                    th { background-color: #333; color: white; }
                    .error { background-color: #ffcccc; }
                    .warn { background-color: #ffffcc; }
                    .info { background-color: #ccffcc; }
                    .debug { background-color: #ccccff; }
                    .verbose { background-color: #ffffff; }
                </style>
            </head>
            <body>
                <table>
                    <thead><tr><th>Timestamp</th><th>Level</th><th>Tag</th><th>Message</th></tr></thead>
                    <tbody>${rows.joinToString("")}</tbody>
                </table>
            </body>
            </html>
        """.trimIndent()
    }
    
    private fun escapeJson(text: String): String = text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
    
    private fun escapeCsv(text: String): String = text.replace("\"", "\"\"")
    
    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}

class DefaultLogParser : LogParser {
    
    private val logcatPattern = Regex(
        """^(\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}\.\d{3})\s+(\d+)\s+(\d+)\s+([VDIWEA])\s+(.+?):\s+(.*)$"""
    )
    
    override fun parseLine(line: String): LogEntry? {
        val match = logcatPattern.find(line) ?: return null
        
        val timestamp = parseTimestamp(match.groupValues[1])
        val pid = match.groupValues[2].toIntOrNull()
        val tid = match.groupValues[3].toIntOrNull()
        val level = parseLevel(match.groupValues[4])
        val tag = match.groupValues[5]
        val message = match.groupValues[6]
        
        return LogEntry(
            id = 0,
            timestamp = timestamp,
            level = level,
            tag = tag,
            message = message,
            pid = pid,
            tid = tid,
            source = LogSource.APP
        )
    }
    
    override fun parseLogcat(output: String): List<LogEntry> {
        return output.lines()
            .mapNotNull { parseLine(it) }
    }
    
    private fun parseTimestamp(timestampStr: String): Long {
        return try {
            val format = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
            format.parse(timestampStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun parseLevel(levelChar: String): LogLevel {
        return when (levelChar) {
            "V" -> LogLevel.VERBOSE
            "D" -> LogLevel.DEBUG
            "I" -> LogLevel.INFO
            "W" -> LogLevel.WARN
            "E" -> LogLevel.ERROR
            "A" -> LogLevel.ASSERT
            else -> LogLevel.VERBOSE
        }
    }
}
