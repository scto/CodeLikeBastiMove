package com.scto.codelikebastimove.feature.tooling.impl.bridge

import com.scto.codelikebastimove.feature.tooling.api.bridge.*
import com.scto.codelikebastimove.feature.tooling.api.gradle.GradleBuildResult
import com.scto.codelikebastimove.feature.tooling.api.gradle.GradleOutputProvider
import com.scto.codelikebastimove.feature.tooling.api.log.LogViewProvider
import com.scto.codelikebastimove.feature.tooling.api.terminal.TerminalBridge
import com.scto.codelikebastimove.feature.tooling.impl.gradle.DefaultGradleOutputProvider
import com.scto.codelikebastimove.feature.tooling.impl.log.DefaultLogViewProvider
import com.scto.codelikebastimove.feature.tooling.impl.terminal.DefaultTerminalBridge
import kotlinx.coroutines.flow.*

class DefaultToolingBridge(private val config: ToolingConfig) : ToolingBridge {

  override val gradleProvider: GradleOutputProvider =
    DefaultGradleOutputProvider(config.projectPath)
  override val logProvider: LogViewProvider = DefaultLogViewProvider(config.logConfig)
  override val terminalProvider: TerminalBridge = DefaultTerminalBridge(config.terminalConfig)

  private val _activeTool = MutableStateFlow(ActiveTool.TERMINAL)
  override val activeTool: StateFlow<ActiveTool> = _activeTool.asStateFlow()

  private val _unifiedOutput = MutableSharedFlow<UnifiedOutputLine>(replay = 100)

  override suspend fun initialize(): Result<Unit> {
    return runCatching {
      terminalProvider.connect()
      gradleProvider.startCapture()
      logProvider.startCapture()
    }
  }

  override suspend fun shutdown(): Result<Unit> {
    return runCatching {
      terminalProvider.disconnect()
      gradleProvider.stopCapture()
      logProvider.stopCapture()
    }
  }

  override fun switchTool(tool: ActiveTool) {
    _activeTool.value = tool
  }

  override suspend fun runGradleTask(taskPath: String): Flow<GradleBuildResult> {
    return runGradleTasks(listOf(taskPath))
  }

  override suspend fun runGradleTasks(taskPaths: List<String>): Flow<GradleBuildResult> = flow {
    val command = buildGradleCommand(taskPaths)
    val result = terminalProvider.executeCommand(command)

    val buildResult =
      GradleBuildResult(
        success = result.isSuccess,
        exitCode = result.exitCode,
        output = emptyList(),
        errors = emptyList(),
        warnings = emptyList(),
        duration = result.duration,
        tasksExecuted = taskPaths,
        tasksFailed = if (!result.isSuccess) taskPaths else emptyList(),
      )

    emit(buildResult)
  }

  override suspend fun executeInTerminal(command: String): Result<Unit> {
    return terminalProvider.sendInput("$command\n")
  }

  override fun getUnifiedOutput(): Flow<UnifiedOutputLine> = _unifiedOutput.asSharedFlow()

  private fun buildGradleCommand(tasks: List<String>): String {
    val wrapper =
      if (config.gradleWrapper.startsWith("/")) {
        config.gradleWrapper
      } else {
        "${config.projectPath}/${config.gradleWrapper}"
      }

    return "$wrapper ${tasks.joinToString(" ")}"
  }

  companion object {
    @Volatile private var INSTANCE: DefaultToolingBridge? = null

    fun getInstance(config: ToolingConfig): DefaultToolingBridge {
      return INSTANCE
        ?: synchronized(this) { INSTANCE ?: DefaultToolingBridge(config).also { INSTANCE = it } }
    }
  }
}

class DefaultToolingBridgeFactory : ToolingBridgeFactory {
  override fun create(config: ToolingConfig): ToolingBridge {
    return DefaultToolingBridge.getInstance(config)
  }
}
