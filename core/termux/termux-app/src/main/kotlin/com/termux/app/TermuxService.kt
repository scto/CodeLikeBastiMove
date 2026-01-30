package com.termux.app

import com.termux.app.terminal.TermuxTerminalSession
import com.termux.terminal.TerminalSessionClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TermuxService {

  companion object {
    private const val TAG = "TermuxService"

    @Volatile private var INSTANCE: TermuxService? = null

    fun getInstance(): TermuxService {
      return INSTANCE ?: synchronized(this) { INSTANCE ?: TermuxService().also { INSTANCE = it } }
    }
  }

  private val _sessions = MutableStateFlow<List<TermuxTerminalSession>>(emptyList())
  val sessions: StateFlow<List<TermuxTerminalSession>> = _sessions.asStateFlow()

  private val _currentSession = MutableStateFlow<TermuxTerminalSession?>(null)
  val currentSession: StateFlow<TermuxTerminalSession?> = _currentSession.asStateFlow()

  private var sessionIdCounter = 0

  fun createSession(
    client: TerminalSessionClient,
    workingDirectory: String = "/",
    shell: String = "/system/bin/sh",
    sessionName: String? = null,
  ): TermuxTerminalSession {
    val session =
      TermuxTerminalSession.createDefaultSession(
        client = client,
        workingDirectory = workingDirectory,
        shell = shell,
      )

    session.sessionName = sessionName ?: "Session ${++sessionIdCounter}"

    _sessions.value = _sessions.value + session

    if (_currentSession.value == null) {
      _currentSession.value = session
    }

    return session
  }

  fun switchToSession(session: TermuxTerminalSession) {
    if (_sessions.value.contains(session)) {
      _currentSession.value = session
    }
  }

  fun switchToSession(index: Int) {
    val sessionsList = _sessions.value
    if (index in sessionsList.indices) {
      _currentSession.value = sessionsList[index]
    }
  }

  fun removeSession(session: TermuxTerminalSession) {
    session.finishIfRunning()
    session.cleanupResources()

    val updatedSessions = _sessions.value.filter { it != session }
    _sessions.value = updatedSessions

    if (_currentSession.value == session) {
      _currentSession.value = updatedSessions.firstOrNull()
    }
  }

  fun removeAllSessions() {
    _sessions.value.forEach { session ->
      session.finishIfRunning()
      session.cleanupResources()
    }
    _sessions.value = emptyList()
    _currentSession.value = null
  }

  fun getSessionCount(): Int = _sessions.value.size

  fun getSessionAt(index: Int): TermuxTerminalSession? {
    return _sessions.value.getOrNull(index)
  }

  fun getCurrentSessionIndex(): Int {
    val current = _currentSession.value ?: return -1
    return _sessions.value.indexOf(current)
  }

  fun executeCommand(command: String, session: TermuxTerminalSession? = null) {
    val targetSession = session ?: _currentSession.value ?: return
    targetSession.write("$command\n")
  }

  fun sendInput(input: String, session: TermuxTerminalSession? = null) {
    val targetSession = session ?: _currentSession.value ?: return
    targetSession.write(input)
  }

  fun sendControlKey(key: Char, session: TermuxTerminalSession? = null) {
    val targetSession = session ?: _currentSession.value ?: return
    val controlCode = (key.uppercaseChar() - 'A' + 1).toByte()
    targetSession.write(byteArrayOf(controlCode))
  }
}
