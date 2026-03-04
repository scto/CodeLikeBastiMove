package com.scto.codelikebastimove.core.logger

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

/**
 * Ein thread-sicherer Logger mit Unterstützung für Logcat und asynchrones Datei-Logging.
 * Verwendet einen Single-Thread-Executor, um Schreibvorgänge ohne UI-Blockierung zu verarbeiten.
 */
object CLBMLogger {

    private var isLoggingEnabled: Boolean = true
    private var logFile: File? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Initialisiert den Logger und erstellt bei Bedarf das Log-Verzeichnis.
     */
    fun initialize(context: Context, enabled: Boolean) {
        this.isLoggingEnabled = enabled
        
        try {
            val logDir = File(context.filesDir, "logs")
            if (!logDir.exists()) logDir.mkdirs()

            val fileName = "clbm_${fileNameFormat.format(Date())}.log"
            logFile = File(logDir, fileName)
            
            i("CLBMLogger", "Logging-Dienst gestartet. Datei: ${logFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("CLBMLogger", "Fehler bei der Initialisierung der Log-Datei", e)
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.isLoggingEnabled = enabled
    }

    // --- Log Methoden für verschiedene Level ---

    fun d(tag: String, msg: String) = log(Level.DEBUG, tag, msg)
    fun i(tag: String, msg: String) = log(Level.INFO, tag, msg)
    fun w(tag: String, msg: String) = log(Level.WARN, tag, msg)
    fun e(tag: String, msg: String, tr: Throwable? = null) = log(Level.ERROR, tag, msg, tr)
    fun fatal(tag: String, msg: String, tr: Throwable? = null) = log(Level.FATAL, tag, msg, tr)

    private fun log(level: Level, tag: String, msg: String, tr: Throwable? = null) {
        if (!isLoggingEnabled) return

        val timestamp = timeFormat.format(Date())
        val logLine = "[$timestamp] [${Thread.currentThread().name}] ${level.name}/$tag: $msg"

        // Konsolen-Output (Logcat)
        when (level) {
            Level.DEBUG -> Log.d(tag, msg)
            Level.INFO -> Log.i(tag, msg)
            Level.WARN -> Log.w(tag, msg)
            Level.ERROR -> Log.e(tag, msg, tr)
            Level.FATAL -> Log.wtf(tag, "!!! FATAL ERROR !!! $msg", tr)
        }

        // Asynchrone Datei-Ausgabe (Thread-Safe)
        executor.execute {
            try {
                logFile?.let { file ->
                    FileWriter(file, true).use { fw ->
                        PrintWriter(fw).use { pw ->
                            pw.println(logLine)
                            tr?.let {
                                pw.println("Stacktrace:")
                                it.printStackTrace(pw)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CLBMLogger", "Kritischer Fehler beim Schreiben in die Log-Datei", e)
            }
        }
    }

    /**
     * Entfernt Log-Dateien, die älter als der angegebene Zeitraum sind.
     */
    fun cleanOldLogs(context: Context, days: Int = 7) {
        executor.execute {
            val logDir = File(context.filesDir, "logs")
            val threshold = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
            logDir.listFiles()?.forEach { 
                if (it.lastModified() < threshold) {
                    it.delete() 
                    Log.d("CLBMLogger", "Alte Log-Datei gelöscht: ${it.name}")
                }
            }
        }
    }

    private enum class Level { DEBUG, INFO, WARN, ERROR, FATAL }
}