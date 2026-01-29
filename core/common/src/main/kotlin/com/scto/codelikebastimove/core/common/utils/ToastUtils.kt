package com.scto.codelikebastimove.core.common.utils

import android.content.Context
import android.widget.Toast

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

// -----------------------------------------------------------------------------
// Context Extensions (Ideal für onClick Listener in Compose)
// -----------------------------------------------------------------------------

fun Context.showShortToast(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showShortToast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.showLongToast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

// -----------------------------------------------------------------------------
// Compose-Specific Helper (Reagieren auf State-Änderungen)
// -----------------------------------------------------------------------------

/**
 * Ein Composable-Effekt, der einen Toast anzeigt, wenn sich [message] ändert.
 * Ideal für Fehlermeldungen aus einem ViewModel.
 *
 * Verwendung:
 * ```
 * ToastEffect(viewModel.errorMessage) {
 * viewModel.clearError() // State zurücksetzen, damit Toast nicht erneut erscheint
 * }
 * ```
 */
@Composable
fun ToastEffect(
    message: String?,
    duration: Int = Toast.LENGTH_SHORT,
    onShown: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        if (!message.isNullOrEmpty()) {
            context.showShortToast(message) // Nutzt Extension von oben
            onShown()
        }
    }
}

/**
 * Überladung für String-Resources (Int IDs).
 */
@Composable
fun ToastEffect(
    @StringRes messageResId: Int?,
    duration: Int = Toast.LENGTH_SHORT,
    onShown: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(messageResId) {
        if (messageResId != null && messageResId != 0) {
            if (duration == Toast.LENGTH_LONG) {
                context.showLongToast(messageResId)
            } else {
                context.showShortToast(messageResId)
            }
            onShown()
        }
    }
}

// -----------------------------------------------------------------------------
// Legacy Support (Damit alter Code weiterhin funktioniert)
// -----------------------------------------------------------------------------

fun showShortToast(context: Context, text: CharSequence) {
    context.showShortToast(text)
}

fun showLongToast(context: Context, text: CharSequence) {
    context.showLongToast(text)
}