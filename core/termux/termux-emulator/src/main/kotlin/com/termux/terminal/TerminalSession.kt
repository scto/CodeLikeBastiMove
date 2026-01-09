package com.termux.terminal

import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Field

abstract class TerminalSession(
    private val shellPath: String,
    private val cwd: String,
    private val args: Array<String>,
    private val env: Array<String>,
    private val transcriptRows: Int,
    private val client: TerminalSessionClient
) : Runnable {
    
    companion object {
        private const val TAG = "TerminalSession"
        private var sessionIdCounter = 0
    }
    
    val sessionId: Int = ++sessionIdCounter
    var sessionName: String? = null
    
    private var emulator: TerminalEmulator? = null
    private var terminalToProcessIOPumpThread: Thread? = null
    private var processToTerminalIOPumpThread: Thread? = null
    
    private var processId: Int = -1
    private var terminalFileDescriptor: Int = -1
    
    private var terminalInput: FileOutputStream? = null
    private var terminalOutput: FileInputStream? = null
    
    @Volatile
    private var isRunning: Boolean = false
    
    var exitStatus: Int = 0
        private set
    
    fun getEmulator(): TerminalEmulator? = emulator
    
    fun isRunning(): Boolean = isRunning
    
    abstract fun initializeEmulator(columns: Int, rows: Int): Boolean
    
    fun updateSize(columns: Int, rows: Int) {
        emulator?.resize(columns, rows)
        notifySizeChanged(columns, rows)
    }
    
    fun getScreen(): TerminalBuffer? = emulator?.getScreen()
    
    fun getTitle(): String = emulator?.title ?: ""
    
    fun write(data: ByteArray) {
        try {
            terminalInput?.write(data)
            terminalInput?.flush()
        } catch (e: IOException) {
        }
    }
    
    fun write(data: String) {
        write(data.toByteArray())
    }
    
    fun writeCodePoint(codePoint: Int) {
        if (codePoint > 0xFFFF) {
            write(String(intArrayOf(codePoint), 0, 1))
        } else {
            write(byteArrayOf((codePoint and 0xFF).toByte()))
        }
    }
    
    fun finishIfRunning() {
        if (isRunning) {
            isRunning = false
        }
    }
    
    open fun cleanupResources() {
        try {
            terminalInput?.close()
            terminalOutput?.close()
        } catch (e: IOException) {
        }
    }
    
    fun reset() {
        emulator?.reset()
        notifyScreenChanged()
    }
    
    override fun run() {
        isRunning = true
        client.onSessionStarted()
        
        while (isRunning) {
            val buffer = ByteArray(4096)
            try {
                val bytesRead = terminalOutput?.read(buffer) ?: -1
                if (bytesRead > 0) {
                    emulator?.process(buffer, bytesRead)
                    notifyScreenChanged()
                } else if (bytesRead == -1) {
                    break
                }
            } catch (e: IOException) {
                break
            }
        }
        
        isRunning = false
        client.onSessionFinished()
    }
    
    protected fun setEmulator(emulator: TerminalEmulator) {
        this.emulator = emulator
    }
    
    protected fun setTerminalIO(input: FileOutputStream, output: FileInputStream) {
        this.terminalInput = input
        this.terminalOutput = output
    }
    
    protected fun setProcessId(pid: Int) {
        this.processId = pid
    }
    
    fun notifyTitleChanged() {
        client.onTitleChanged(this)
    }
    
    fun notifyBell() {
        client.onBell(this)
    }
    
    fun notifyScreenChanged() {
        client.onTextChanged(this)
    }
    
    fun notifySizeChanged(columns: Int, rows: Int) {
        client.onSessionSizeChanged(this, columns, rows)
    }
    
    fun notifyClipboardText(text: String) {
        client.onClipboardText(this, text)
    }
}

interface TerminalSessionClient {
    fun onTextChanged(session: TerminalSession)
    fun onTitleChanged(session: TerminalSession)
    fun onSessionFinished(session: TerminalSession)
    fun onSessionStarted()
    fun onBell(session: TerminalSession)
    fun onClipboardText(session: TerminalSession, text: String)
    fun onSessionSizeChanged(session: TerminalSession, columns: Int, rows: Int)
    
    fun logError(tag: String, message: String)
    fun logWarn(tag: String, message: String)
    fun logInfo(tag: String, message: String)
    fun logDebug(tag: String, message: String)
    fun logVerbose(tag: String, message: String)
    fun logStackTrace(tag: String, e: Exception)
}
