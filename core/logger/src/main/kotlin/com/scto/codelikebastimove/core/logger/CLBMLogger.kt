package com.scto.codelikebastimove.core.logger

import android.util.Log

object CLBMLogger {

  @Volatile private var isEnabled: Boolean = BuildConfig.LOGGING_DEFAULT_ENABLED

  fun initialize(enabled: Boolean = BuildConfig.LOGGING_DEFAULT_ENABLED) {
    isEnabled = enabled
  }

  fun setEnabled(enabled: Boolean) {
    isEnabled = enabled
  }

  fun isLoggingEnabled(): Boolean = isEnabled

  fun v(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.v(tag, message, throwable)
      } else {
        Log.v(tag, message)
      }
    }
  }

  fun d(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.d(tag, message, throwable)
      } else {
        Log.d(tag, message)
      }
    }
  }

  fun i(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.i(tag, message, throwable)
      } else {
        Log.i(tag, message)
      }
    }
  }

  fun w(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.w(tag, message, throwable)
      } else {
        Log.w(tag, message)
      }
    }
  }

  fun e(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.e(tag, message, throwable)
      } else {
        Log.e(tag, message)
      }
    }
  }

  fun wtf(tag: String, message: String, throwable: Throwable? = null) {
    if (isEnabled) {
      if (throwable != null) {
        Log.wtf(tag, message, throwable)
      } else {
        Log.wtf(tag, message)
      }
    }
  }
}

inline fun <reified T> T.logV(message: String, throwable: Throwable? = null) {
  CLBMLogger.v(T::class.java.simpleName, message, throwable)
}

inline fun <reified T> T.logD(message: String, throwable: Throwable? = null) {
  CLBMLogger.d(T::class.java.simpleName, message, throwable)
}

inline fun <reified T> T.logI(message: String, throwable: Throwable? = null) {
  CLBMLogger.i(T::class.java.simpleName, message, throwable)
}

inline fun <reified T> T.logW(message: String, throwable: Throwable? = null) {
  CLBMLogger.w(T::class.java.simpleName, message, throwable)
}

inline fun <reified T> T.logE(message: String, throwable: Throwable? = null) {
  CLBMLogger.e(T::class.java.simpleName, message, throwable)
}

fun logV(tag: String, message: String, throwable: Throwable? = null) {
  CLBMLogger.v(tag, message, throwable)
}

fun logD(tag: String, message: String, throwable: Throwable? = null) {
  CLBMLogger.d(tag, message, throwable)
}

fun logI(tag: String, message: String, throwable: Throwable? = null) {
  CLBMLogger.i(tag, message, throwable)
}

fun logW(tag: String, message: String, throwable: Throwable? = null) {
  CLBMLogger.w(tag, message, throwable)
}

fun logE(tag: String, message: String, throwable: Throwable? = null) {
  CLBMLogger.e(tag, message, throwable)
}
