package com.scto.codelikebastimove.features.tooling.impl.gradle

import com.scto.codelikebastimove.features.tooling.api.gradle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultGradleOutputProvider(
    private val projectPath: String
) : GradleOutputProvider {
    
    private val _buildOutput = MutableStateFlow<List<GradleOutputLine>>(emptyList())
    override val buildOutput: StateFlow<List<GradleOutputLine>> = _buildOutput.asStateFlow()
    
    private val _buildState = MutableStateFlow(BuildState.IDLE)
    override val buildState: StateFlow<BuildState> = _buildState.asStateFlow()
    
    private var lineCounter = 0
    private var isCapturing = false
    
    private val listeners = mutableListOf<GradleBuildListener>()
    
    override fun startCapture() {
        isCapturing = true
        _buildState.value = BuildState.CONFIGURING
    }
    
    override fun stopCapture() {
        isCapturing = false
        _buildState.value = BuildState.IDLE
    }
    
    override fun clearOutput() {
        _buildOutput.value = emptyList()
        lineCounter = 0
    }
    
    override fun getOutputAsText(): String {
        return _buildOutput.value.joinToString("\n") { it.content }
    }
    
    fun appendOutput(content: String, level: OutputLevel = OutputLevel.INFO, task: String? = null) {
        if (!isCapturing) return
        
        val line = GradleOutputLine(
            lineNumber = ++lineCounter,
            content = content,
            level = level,
            task = task,
            phase = detectPhase(content)
        )
        
        _buildOutput.value = _buildOutput.value + line
        
        listeners.forEach { it.onOutputLine(line) }
        
        detectError(content)?.let { error ->
            listeners.forEach { it.onError(error) }
        }
    }
    
    fun setBuildState(state: BuildState) {
        _buildState.value = state
        
        when (state) {
            BuildState.BUILDING -> listeners.forEach { it.onBuildStarted() }
            BuildState.SUCCESS, BuildState.FAILED -> {
                val result = GradleBuildResult(
                    success = state == BuildState.SUCCESS,
                    exitCode = if (state == BuildState.SUCCESS) 0 else 1,
                    output = _buildOutput.value,
                    errors = emptyList(),
                    warnings = emptyList(),
                    duration = 0L,
                    tasksExecuted = emptyList(),
                    tasksFailed = emptyList()
                )
                listeners.forEach { it.onBuildFinished(result) }
            }
            else -> {}
        }
    }
    
    fun addListener(listener: GradleBuildListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: GradleBuildListener) {
        listeners.remove(listener)
    }
    
    private fun detectPhase(content: String): BuildPhase {
        return when {
            content.contains("Configure project") -> BuildPhase.CONFIGURATION
            content.contains("> Task :") -> BuildPhase.TASK_EXECUTION
            content.contains("Test") && content.contains("PASSED") -> BuildPhase.TEST_EXECUTION
            content.contains("Test") && content.contains("FAILED") -> BuildPhase.TEST_EXECUTION
            content.contains("BUILD SUCCESSFUL") -> BuildPhase.FINALIZATION
            content.contains("BUILD FAILED") -> BuildPhase.FINALIZATION
            else -> BuildPhase.UNKNOWN
        }
    }
    
    private fun detectError(content: String): GradleError? {
        if (!content.contains("error:", ignoreCase = true) && 
            !content.contains("Error:", ignoreCase = true)) {
            return null
        }
        
        val filePattern = Regex("""(.+\.(?:kt|java|xml|gradle(?:\.kts)?)):(\d+)(?::(\d+))?:?\s*(.+)""")
        val match = filePattern.find(content)
        
        return if (match != null) {
            GradleError(
                message = match.groupValues[4],
                file = match.groupValues[1],
                line = match.groupValues[2].toIntOrNull(),
                column = match.groupValues[3].toIntOrNull(),
                task = null
            )
        } else {
            GradleError(
                message = content,
                file = null,
                line = null,
                column = null,
                task = null
            )
        }
    }
}
