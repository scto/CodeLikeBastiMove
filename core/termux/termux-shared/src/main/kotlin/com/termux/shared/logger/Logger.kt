package com.termux.shared.logger

import android.util.Log

object Logger {
  private const val DEFAULT_TAG = "Termux"

  var currentLogLevel = LogLevel.VERBOSE

  enum class LogLevel(val priority: Int) {
    OFF(Int.MAX_VALUE),
    ERROR(Log.ERROR),
    WARN(Log.WARN),
    INFO(Log.INFO),
    DEBUG(Log.DEBUG),
    VERBOSE(Log.VERBOSE),
  }

  fun logVerbose(tag: String = DEFAULT_TAG, message: String) {
    if (currentLogLevel.priority <= LogLevel.VERBOSE.priority) {
      Log.v(tag, message)
    }
  }

  fun logDebug(tag: String = DEFAULT_TAG, message: String) {
    if (currentLogLevel.priority <= LogLevel.DEBUG.priority) {
      Log.d(tag, message)
    }
  }

  fun logInfo(tag: String = DEFAULT_TAG, message: String) {
    if (currentLogLevel.priority <= LogLevel.INFO.priority) {
      Log.i(tag, message)
    }
  }

  fun logWarn(tag: String = DEFAULT_TAG, message: String) {
    if (currentLogLevel.priority <= LogLevel.WARN.priority) {
      Log.w(tag, message)
    }
  }

  fun logError(tag: String = DEFAULT_TAG, message: String) {
    if (currentLogLevel.priority <= LogLevel.ERROR.priority) {
      Log.e(tag, message)
    }
  }

  fun logError(tag: String = DEFAULT_TAG, message: String, throwable: Throwable) {
    if (currentLogLevel.priority <= LogLevel.ERROR.priority) {
      Log.e(tag, message, throwable)
    }
  }

  fun logStackTrace(tag: String = DEFAULT_TAG, throwable: Throwable) {
    if (currentLogLevel.priority <= LogLevel.ERROR.priority) {
      Log.e(tag, Log.getStackTraceString(throwable))
    }
  }

  fun setLogLevel(level: LogLevel) {
    currentLogLevel = level
  }

  fun getLogLevelFromString(levelString: String?): LogLevel {
    return when (levelString?.uppercase()) {
      "OFF" -> LogLevel.OFF
      "ERROR" -> LogLevel.ERROR
      "WARN",
      "WARNING" -> LogLevel.WARN
      "INFO" -> LogLevel.INFO
      "DEBUG" -> LogLevel.DEBUG
      "VERBOSE" -> LogLevel.VERBOSE
      else -> LogLevel.VERBOSE
    }
  }
}
