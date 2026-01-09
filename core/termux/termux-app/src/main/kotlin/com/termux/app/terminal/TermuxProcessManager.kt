package com.termux.app.terminal

import android.os.Build
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import java.io.File
import java.io.FileDescriptor
import java.io.IOException

object TermuxProcessManager {
    
    private const val TAG = "TermuxProcessManager"
    
    init {
        try {
            System.loadLibrary("termux-jni")
        } catch (e: UnsatisfiedLinkError) {
        }
    }
    
    external fun createSubprocess(
        cmd: String,
        cwd: String,
        args: Array<String>,
        envVars: Array<String>,
        rows: Int,
        columns: Int
    ): IntArray
    
    external fun setPtyWindowSize(fd: Int, rows: Int, columns: Int)
    
    external fun waitFor(pid: Int): Int
    
    external fun close(fd: Int)
    
    external fun kill(pid: Int, signal: Int): Int
    
    fun createProcess(
        command: String,
        workingDirectory: String,
        arguments: Array<String>,
        environment: Array<String>,
        rows: Int,
        columns: Int
    ): ProcessResult {
        return try {
            val result = createSubprocess(command, workingDirectory, arguments, environment, rows, columns)
            
            if (result.size >= 2) {
                ProcessResult(
                    pid = result[0],
                    fileDescriptor = result[1],
                    success = true,
                    errorMessage = null
                )
            } else {
                ProcessResult(
                    pid = -1,
                    fileDescriptor = -1,
                    success = false,
                    errorMessage = "Failed to create subprocess"
                )
            }
        } catch (e: Exception) {
            ProcessResult(
                pid = -1,
                fileDescriptor = -1,
                success = false,
                errorMessage = e.message
            )
        }
    }
    
    fun createProcessWithProcessBuilder(
        command: String,
        workingDirectory: String,
        arguments: Array<String>,
        environment: Map<String, String>
    ): FallbackProcessResult {
        return try {
            val processBuilder = ProcessBuilder(listOf(command) + arguments.toList())
            processBuilder.directory(File(workingDirectory))
            processBuilder.environment().putAll(environment)
            processBuilder.redirectErrorStream(false)
            
            val process = processBuilder.start()
            
            FallbackProcessResult(
                process = process,
                success = true,
                errorMessage = null
            )
        } catch (e: IOException) {
            FallbackProcessResult(
                process = null,
                success = false,
                errorMessage = e.message
            )
        }
    }
    
    fun resizeTerminal(fd: Int, rows: Int, columns: Int) {
        try {
            setPtyWindowSize(fd, rows, columns)
        } catch (e: Exception) {
        }
    }
    
    fun terminateProcess(pid: Int, graceful: Boolean = true): Boolean {
        return try {
            val signal = if (graceful) OsConstants.SIGTERM else OsConstants.SIGKILL
            kill(pid, signal)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun isProcessRunning(pid: Int): Boolean {
        return try {
            kill(pid, 0) == 0
        } catch (e: Exception) {
            false
        }
    }
}

data class ProcessResult(
    val pid: Int,
    val fileDescriptor: Int,
    val success: Boolean,
    val errorMessage: String?
)

data class FallbackProcessResult(
    val process: Process?,
    val success: Boolean,
    val errorMessage: String?
)

class TerminalPtySession(
    private val columns: Int,
    private val rows: Int,
    private val command: String,
    private val workingDirectory: String,
    private val environment: Array<String>
) {
    private var pid: Int = -1
    private var fd: Int = -1
    
    @Volatile
    private var isRunning = false
    
    fun start(): Boolean {
        val result = TermuxProcessManager.createProcess(
            command = command,
            workingDirectory = workingDirectory,
            arguments = arrayOf(command),
            environment = environment,
            rows = rows,
            columns = columns
        )
        
        if (result.success) {
            pid = result.pid
            fd = result.fileDescriptor
            isRunning = true
        }
        
        return result.success
    }
    
    fun resize(newColumns: Int, newRows: Int) {
        if (fd >= 0) {
            TermuxProcessManager.resizeTerminal(fd, newRows, newColumns)
        }
    }
    
    fun write(data: ByteArray) {
        if (fd >= 0) {
            try {
                Os.write(FileDescriptor(), data, 0, data.size)
            } catch (e: ErrnoException) {
            }
        }
    }
    
    fun stop(graceful: Boolean = true) {
        if (pid > 0) {
            TermuxProcessManager.terminateProcess(pid, graceful)
        }
        if (fd >= 0) {
            TermuxProcessManager.close(fd)
        }
        isRunning = false
    }
    
    fun isRunning(): Boolean = isRunning && TermuxProcessManager.isProcessRunning(pid)
    
    fun getPid(): Int = pid
    fun getFileDescriptor(): Int = fd
}
