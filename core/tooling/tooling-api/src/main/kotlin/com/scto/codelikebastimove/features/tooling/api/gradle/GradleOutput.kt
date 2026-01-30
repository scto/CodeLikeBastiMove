package com.scto.codelikebastimove.feature.tooling.api.gradle

import kotlinx.coroutines.flow.StateFlow

interface GradleOutputProvider {
  val buildOutput: StateFlow<List<GradleOutputLine>>
  val buildState: StateFlow<BuildState>

  fun startCapture()

  fun stopCapture()

  fun clearOutput()

  fun getOutputAsText(): String
}

data class GradleOutputLine(
  val lineNumber: Int,
  val content: String,
  val level: OutputLevel,
  val timestamp: Long = System.currentTimeMillis(),
  val task: String? = null,
  val phase: BuildPhase = BuildPhase.UNKNOWN,
)

enum class OutputLevel {
  INFO,
  DEBUG,
  WARNING,
  ERROR,
  LIFECYCLE,
  QUIET,
}

enum class BuildState {
  IDLE,
  CONFIGURING,
  BUILDING,
  EXECUTING_TASKS,
  SUCCESS,
  FAILED,
  CANCELLED,
}

enum class BuildPhase {
  UNKNOWN,
  CONFIGURATION,
  TASK_EXECUTION,
  TEST_EXECUTION,
  FINALIZATION,
}

data class GradleBuildResult(
  val success: Boolean,
  val exitCode: Int,
  val output: List<GradleOutputLine>,
  val errors: List<GradleError>,
  val warnings: List<GradleWarning>,
  val duration: Long,
  val tasksExecuted: List<String>,
  val tasksFailed: List<String>,
)

data class GradleError(
  val message: String,
  val file: String?,
  val line: Int?,
  val column: Int?,
  val task: String?,
)

data class GradleWarning(val message: String, val file: String?, val line: Int?, val task: String?)

interface GradleBuildListener {
  fun onBuildStarted()

  fun onBuildFinished(result: GradleBuildResult)

  fun onTaskStarted(taskPath: String)

  fun onTaskFinished(taskPath: String, success: Boolean)

  fun onOutputLine(line: GradleOutputLine)

  fun onError(error: GradleError)
}
