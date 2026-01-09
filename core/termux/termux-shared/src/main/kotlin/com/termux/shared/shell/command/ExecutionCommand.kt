package com.termux.shared.shell.command

import java.io.File

data class ExecutionCommand(
    val id: Int,
    val executable: String?,
    val arguments: Array<String>?,
    val stdin: String?,
    val workingDirectory: String?,
    val runner: Runner = Runner.TERMINAL_SESSION,
    val isPluginExecutionCommand: Boolean = false,
    val commandLabel: String? = null,
    val commandDescription: String? = null,
    val commandHelp: String? = null,
    val backgroundCustomLogLevel: Int = 0,
    val sessionAction: Int = 0,
    val shellName: String? = null
) {
    enum class Runner {
        TERMINAL_SESSION,
        APP_SHELL,
        ADB_SHELL
    }
    
    enum class State {
        PRE_EXECUTION,
        EXECUTING,
        EXECUTED,
        SUCCESS,
        FAILED
    }
    
    var currentState: State = State.PRE_EXECUTION
        private set
    
    var exitCode: Int = -1
        private set
    
    var stdout: String? = null
        private set
    
    var stderr: String? = null
        private set
    
    var errorMessage: String? = null
        private set
    
    fun setExecuting() {
        currentState = State.EXECUTING
    }
    
    fun setExecuted(exitCode: Int, stdout: String?, stderr: String?) {
        this.exitCode = exitCode
        this.stdout = stdout
        this.stderr = stderr
        currentState = State.EXECUTED
    }
    
    fun setSuccess() {
        currentState = State.SUCCESS
    }
    
    fun setFailed(errorMessage: String?) {
        this.errorMessage = errorMessage
        currentState = State.FAILED
    }
    
    fun hasExecuted(): Boolean = currentState == State.EXECUTED || 
                                  currentState == State.SUCCESS || 
                                  currentState == State.FAILED
    
    fun isSuccessful(): Boolean = currentState == State.SUCCESS || 
                                   (hasExecuted() && exitCode == 0)
    
    fun getCommandWithArguments(): String {
        val sb = StringBuilder()
        executable?.let { sb.append(it) }
        arguments?.forEach { arg ->
            sb.append(" ").append(arg)
        }
        return sb.toString()
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as ExecutionCommand
        
        if (id != other.id) return false
        if (executable != other.executable) return false
        if (arguments != null) {
            if (other.arguments == null) return false
            if (!arguments.contentEquals(other.arguments)) return false
        } else if (other.arguments != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (executable?.hashCode() ?: 0)
        result = 31 * result + (arguments?.contentHashCode() ?: 0)
        return result
    }
    
    companion object {
        fun createEmpty(): ExecutionCommand = ExecutionCommand(
            id = 0,
            executable = null,
            arguments = null,
            stdin = null,
            workingDirectory = null
        )
    }
}
