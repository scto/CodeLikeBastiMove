package com.scto.codelikebastimove.core.common.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.scto.codelikebastimove.core.common.file.File
import java.io.File as JFile

fun <T> Context.open(clazz: Class<T>, newTask: Boolean = false) {
  val intent = Intent(this, clazz)
  if (newTask) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

  startActivity(intent, getEmptyActivityBundle())
}

fun Context.getEmptyActivityBundle(): Bundle? {
  return ActivityOptionsCompat.makeCustomAnimation(
      this,
      android.R.anim.fade_in,
      android.R.anim.fade_out,
    )
    .toBundle()
}

fun Context.openFile(file: File) {
  val uri = file.uri(this)
  val mimeType = contentResolver.getType(uri)

  Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, mimeType)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(this)
  }
}

val Context.tmpDir: JFile
  get() {
    return JFile(filesDir.parentFile, "tmp").also {
      if (!it.exists()) {
        it.mkdirs()
      }
    }
  }

val Context.localDir: JFile
  get() {
    return JFile(filesDir.parentFile, "local").also {
      if (!it.exists()) {
        it.mkdirs()
      }
    }
  }

val Context.localBinDir: JFile
  get() {
    return JFile(filesDir.parentFile, "local/bin").also {
      if (!it.exists()) {
        it.mkdirs()
      }
    }
  }

val Context.localLibDir: JFile
  get() {
    return JFile(filesDir.parentFile, "local/lib").also {
      if (!it.exists()) {
        it.mkdirs()
      }
    }
  }

val Context.alpineDir: JFile
  get() {
    return JFile(localDir, "alpine").also {
      if (!it.exists()) {
        it.mkdirs()
      }
    }
  }
