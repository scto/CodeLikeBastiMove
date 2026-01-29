package com.scto.codelikebastimove.core.common.utils

import androidx.compose.runtime.MutableState

import com.blankj.utilcode.util.ThreadUtils.runOnUiThread

import com.scto.codelikebastimove.core.logger.logE
import com.scto.codelikebastimove.core.ui.components.ProgressDialogState

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Calls [CoroutineScope.cancel] only if a job is active in the scope.
 *
 * @param message Optional message describing the cause of the cancellation.
 * @param cause Optional cause of the cancellation.
 * @see cancelIfActive
 * @author Akash Yadav
 */
fun CoroutineScope.cancelIfActive(message: String, cause: Throwable? = null) =
cancelIfActive(CancellationException(message, cause))

/**
 * Calls [CoroutineScope.cancel] only if a job is active in the scope.
 *
 * @param exception Optional cause of the cancellation.
 * @author Akash Yadav
 */
fun CoroutineScope.cancelIfActive(exception: CancellationException? = null) {
    val job = coroutineContext[Job]
    job?.cancel(exception)
}

/**
 * Startet eine neue Coroutine und steuert dabei die Sichtbarkeit eines Compose-ProgressDialogs.
 * Der Dialog wird automatisch ausgeblendet, wenn die Aktion beendet ist.
 *
 * @param dialogState Der MutableState des Compose-Dialogs (muss aus dem UI-Code übergeben werden).
 * @param title Optional: Überschreibt den Titel des Dialogs für diese Aktion.
 * @param message Optional: Überschreibt die Nachricht des Dialogs für diese Aktion.
 * @param context Der CoroutineContext [EmptyCoroutineContext] ist Standard.
 * @param invokeOnCompletion Wird aufgerufen, wenn die [action] beendet ist.
 * @param action Die auszuführende Aktion.
 */
inline fun CoroutineScope.launchWithProgressDialog(
    dialogState: MutableState<ProgressDialogState>,
    title: String? = null,
    message: String? = null,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline invokeOnCompletion: (throwable: Throwable?) -> Unit = {},
    crossinline action: suspend CoroutineScope.() -> Unit,
): Job {

    // Dialog anzeigen (State Update)
    runOnUiThread {
        dialogState.value = dialogState.value.copy(
            isVisible = true,
            title = title ?: dialogState.value.title,
            message = message ?: dialogState.value.message,
            isIndeterminate = true // Standardmäßig indeterminate bei einfachen Launches
        )
    }

    return launch(context) {
        action()
    }
    .also {
        job ->
        job.invokeOnCompletion {
            throwable ->
            // Dialog ausblenden
            runOnUiThread {
                dialogState.value = dialogState.value.copy(isVisible = false)
            }

            // Fehler logging mit dem neuen CLBMLogger
            if (throwable != null && throwable !is CancellationException) {
                logE("Coroutine failed with error", throwable)
            }

            invokeOnCompletion(throwable)
        }
    }
}