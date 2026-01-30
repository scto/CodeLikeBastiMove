package com.scto.codelikebastimove.feature.tooling.impl.terminal

import com.scto.codelikebastimove.feature.tooling.api.bridge.TerminalConfig
import com.scto.codelikebastimove.feature.tooling.api.terminal.*
import com.termux.app.TermuxService
import com.termux.app.terminal.TermuxTerminalSession
import com.termux.app.terminal.TermuxTerminalSessionClientAdapter
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class DefaultTerminalBridge(private val config: TerminalConfig) : TerminalBridge {

  private val _isConnected = MutableStateFlow(false)
  override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

  private val _currentWorkingDirectory = MutableStateFlow(config.initialDirectory)
  override val currentWorkingDirectory: StateFlow<String> = _currentWorkingDirectory.asStateFlow()

  private var termuxService: TermuxService? = null
  private var currentSession: TermuxTerminalSession? = null

  private val environment = mutableMapOf<String, String>().apply { putAll(config.environment) }

  override suspend fun connect(): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatching {
        termuxService = TermuxService.getInstance()

        val client =
          TermuxTerminalSessionClientAdapter(
            onTextChangedCallback = {},
            onTitleChangedCallback = {},
            onSessionFinishedCallback = { _isConnected.value = false },
            onBellCallback = {},
          )

        currentSession =
          termuxService?.createSession(
            client = client,
            workingDirectory = config.initialDirectory,
            shell = config.shell,
          )

        _isConnected.value = true
        _currentWorkingDirectory.value = config.initialDirectory
      }
    }

  override suspend fun disconnect(): Result<Unit> =
    withContext(Dispatchers.IO) {
      runCatching {
        currentSession?.let { session -> termuxService?.removeSession(session) }
        currentSession = null
        _isConnected.value = false
      }
    }

  override suspend fun executeCommand(command: String): CommandResult =
    withContext(Dispatchers.IO) {
      val startTime = System.currentTimeMillis()

      try {
        val process =
          ProcessBuilder()
            .command(config.shell, "-c", command)
            .directory(java.io.File(_currentWorkingDirectory.value))
            .redirectErrorStream(false)
            .start()

        val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
        val stderr = BufferedReader(InputStreamReader(process.errorStream)).readText()

        val exitCode = process.waitFor()
        val duration = System.currentTimeMillis() - startTime

        CommandResult(exitCode = exitCode, stdout = stdout, stderr = stderr, duration = duration)
      } catch (e: Exception) {
        CommandResult(
          exitCode = -1,
          stdout = "",
          stderr = e.message ?: "Unknown error",
          duration = System.currentTimeMillis() - startTime,
        )
      }
    }

  override suspend fun executeCommandAsync(command: String): Flow<CommandOutputLine> = flow {
    val process =
      withContext(Dispatchers.IO) {
        ProcessBuilder()
          .command(config.shell, "-c", command)
          .directory(java.io.File(_currentWorkingDirectory.value))
          .redirectErrorStream(false)
          .start()
      }

    val stdoutReader = BufferedReader(InputStreamReader(process.inputStream))
    val stderrReader = BufferedReader(InputStreamReader(process.errorStream))

    withContext(Dispatchers.IO) {
      var line: String?

      while (stdoutReader.readLine().also { line = it } != null) {
        emit(CommandOutputLine(line!!, OutputStream.STDOUT))
      }

      while (stderrReader.readLine().also { line = it } != null) {
        emit(CommandOutputLine(line!!, OutputStream.STDERR))
      }
    }
  }

  override suspend fun sendInput(input: String): Result<Unit> = runCatching {
    currentSession?.write(input)
  }

  override suspend fun sendInterrupt(): Result<Unit> = runCatching {
    currentSession?.write(byteArrayOf(0x03))
  }

  override suspend fun sendEof(): Result<Unit> = runCatching {
    currentSession?.write(byteArrayOf(0x04))
  }

  override fun getEnvironment(): Map<String, String> = environment.toMap()

  override suspend fun setEnvironmentVariable(key: String, value: String): Result<Unit> =
    runCatching {
      environment[key] = value
    }

  override suspend fun changeDirectory(path: String): Result<Unit> = runCatching {
    val dir = java.io.File(path)
    if (dir.exists() && dir.isDirectory) {
      _currentWorkingDirectory.value = path
      currentSession?.write("cd $path\n")
    } else {
      throw IllegalArgumentException("Directory does not exist: $path")
    }
  }
}

class DefaultTerminalSessionProvider : TerminalSessionProvider {

  private val sessions = mutableMapOf<String, DefaultTerminalBridge>()

  override fun createSession(config: TerminalSessionConfig): TerminalBridge {
    val terminalConfig =
      TerminalConfig(
        shell = config.shell,
        initialDirectory = config.workingDirectory,
        environment = config.environment,
      )

    val bridge = DefaultTerminalBridge(terminalConfig)
    sessions[config.sessionId] = bridge
    return bridge
  }

  override fun destroySession(sessionId: String): Boolean {
    return sessions.remove(sessionId) != null
  }

  override fun getSession(sessionId: String): TerminalBridge? {
    return sessions[sessionId]
  }

  override fun getAllSessions(): List<TerminalBridge> {
    return sessions.values.toList()
  }
}
