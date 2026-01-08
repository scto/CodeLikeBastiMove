package com.scto.codelikebastimove.features.tooling.api.terminal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TerminalBridge {
    val isConnected: StateFlow<Boolean>
    val currentWorkingDirectory: StateFlow<String>
    
    suspend fun connect(): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    
    suspend fun executeCommand(command: String): CommandResult
    suspend fun executeCommandAsync(command: String): Flow<CommandOutputLine>
    
    suspend fun sendInput(input: String): Result<Unit>
    suspend fun sendInterrupt(): Result<Unit>
    suspend fun sendEof(): Result<Unit>
    
    fun getEnvironment(): Map<String, String>
    suspend fun setEnvironmentVariable(key: String, value: String): Result<Unit>
    suspend fun changeDirectory(path: String): Result<Unit>
}

data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val duration: Long
) {
    val isSuccess: Boolean get() = exitCode == 0
}

data class CommandOutputLine(
    val content: String,
    val stream: OutputStream,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OutputStream {
    STDOUT,
    STDERR
}

interface TerminalSessionProvider {
    fun createSession(config: TerminalSessionConfig): TerminalBridge
    fun destroySession(sessionId: String): Boolean
    fun getSession(sessionId: String): TerminalBridge?
    fun getAllSessions(): List<TerminalBridge>
}

data class TerminalSessionConfig(
    val sessionId: String,
    val shell: String = "/system/bin/sh",
    val workingDirectory: String = "/",
    val environment: Map<String, String> = emptyMap(),
    val columns: Int = 80,
    val rows: Int = 24
)
