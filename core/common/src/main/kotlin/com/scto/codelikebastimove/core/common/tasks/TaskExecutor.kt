package com.scto.codelikebastimove.core.common.tasks

import com.blankj.utilcode.util.ThreadUtils
import com.scto.codelikebastimove.core.logger.logE // Import der Extension für Error-Logging
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

object TaskExecutor {

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsync(
    callable: Callable<R>,
    callback: (result: R?) -> Unit = {},
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          // Nutzt die Extension Function, die automatisch "TaskExecutor" als TAG setzt
          logE("An error occurred while executing Callable in background thread.", th)
          return@supplyAsync null
        }
      }
      .whenComplete { result, _ -> ThreadUtils.runOnUiThread { callback(result) } }
  }

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsyncProvideError(
    callable: Callable<R>,
    callback: (result: R?, error: Throwable?) -> Unit = { _, _ -> },
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          logE("An error occurred while executing Callable in background thread.", th)
          throw CompletionException(th)
        }
      }
      .whenComplete { result, throwable ->
        ThreadUtils.runOnUiThread { callback(result, throwable) }
      }
  }
}
