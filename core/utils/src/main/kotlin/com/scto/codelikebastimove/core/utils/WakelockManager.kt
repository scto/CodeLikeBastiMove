package com.scto.codelikebastimove.core.utils

import android.content.Context
import android.os.PowerManager
import com.scto.codelikebastimove.core.logger.CLBMLogger

/**
 * Verwaltet den Wakelock, um Deep Sleep während Builds zu verhindern.
 */
class WakelockManager(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var wakeLock: PowerManager.WakeLock? = null

    fun acquire(tag: String = "CLBM:Wakelock") {
        if (wakeLock?.isHeld == true) return

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag).apply {
            acquire()
        }
        CLBMLogger.i("Wakelock", "Wakelock erworben: $tag")
    }

    fun release() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            wakeLock = null
            CLBMLogger.i("Wakelock", "Wakelock freigegeben")
        }
    }
}