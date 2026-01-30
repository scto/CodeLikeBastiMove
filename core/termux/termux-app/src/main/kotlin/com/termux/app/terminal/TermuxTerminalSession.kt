package com.termux.app.terminal

import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

class TermuxTerminalSession(
  shellPath: String,
  cwd: String,
  args: Array<String>,
  env: Array<String>,
  transcriptRows: Int,
  client: TerminalSessionClient,
) : TerminalSession(shellPath, cwd, args, env, transcriptRows, client) {

  companion object {
    private const val TAG = "TermuxTerminalSession"

    fun createSession(
      shellPath: String,
      workingDirectory: String,
      args: Array<String>,
      environment: Array<String>,
      transcriptRows: Int = TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
      client: TerminalSessionClient,
    ): TermuxTerminalSession {
      return TermuxTerminalSession(
        shellPath = shellPath,
        cwd = workingDirectory,
        args = args,
        env = environment,
        transcriptRows = transcriptRows,
        client = client,
      )
    }

    fun createDefaultSession(
      client: TerminalSessionClient,
      workingDirectory: String = "/",
      shell: String = "/system/bin/sh",
    ): TermuxTerminalSession {
      val env = buildDefaultEnvironment(workingDirectory)

      return createSession(
        shellPath = shell,
        workingDirectory = workingDirectory,
        args = arrayOf(shell),
        environment = env,
        client = client,
      )
    }

    private fun buildDefaultEnvironment(workingDirectory: String): Array<String> {
      val env = mutableListOf<String>()

      env.add("TERM=xterm-256color")
      env.add("HOME=$workingDirectory")
      env.add("PATH=/system/bin:/system/xbin")
      env.add("LANG=en_US.UTF-8")
      env.add("COLORTERM=truecolor")

      return env.toTypedArray()
    }
  }

  private var processId: Int = -1

  override fun initializeEmulator(columns: Int, rows: Int): Boolean {
    val emulator =
      TerminalEmulator(this, columns, rows, TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS)
    setEmulator(emulator)
    return true
  }

  fun startProcess(): Boolean {
    try {
      return true
    } catch (e: Exception) {
      return false
    }
  }

  fun getProcessId(): Int = processId

  override fun cleanupResources() {
    super.cleanupResources()

    if (processId > 0) {
      try {
        android.os.Process.killProcess(processId)
      } catch (e: Exception) {}
    }
  }
}

class TermuxTerminalSessionClientAdapter(
  private val onTextChangedCallback: () -> Unit,
  private val onTitleChangedCallback: (String) -> Unit,
  private val onSessionFinishedCallback: (Int) -> Unit,
  private val onBellCallback: () -> Unit,
) : TerminalSessionClient {

  override fun onTextChanged(session: TerminalSession) {
    onTextChangedCallback()
  }

  override fun onTitleChanged(session: TerminalSession) {
    onTitleChangedCallback(session.getTitle())
  }

  override fun onSessionFinished(session: TerminalSession) {
    onSessionFinishedCallback(session.exitStatus)
  }

  override fun onSessionStarted() {}

  override fun onBell(session: TerminalSession) {
    onBellCallback()
  }

  override fun onClipboardText(session: TerminalSession, text: String) {}

  override fun onSessionSizeChanged(session: TerminalSession, columns: Int, rows: Int) {}

  override fun logError(tag: String, message: String) {
    android.util.Log.e(tag, message)
  }

  override fun logWarn(tag: String, message: String) {
    android.util.Log.w(tag, message)
  }

  override fun logInfo(tag: String, message: String) {
    android.util.Log.i(tag, message)
  }

  override fun logDebug(tag: String, message: String) {
    android.util.Log.d(tag, message)
  }

  override fun logVerbose(tag: String, message: String) {
    android.util.Log.v(tag, message)
  }

  override fun logStackTrace(tag: String, e: Exception) {
    android.util.Log.e(tag, android.util.Log.getStackTraceString(e))
  }
}
