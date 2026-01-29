package com.scto.codelikebastimove.core.common.utils

import android.os.Build
import android.os.FileObserver

import androidx.compose.runtime.mutableStateOf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import java.io.File

object FileManager {
    private lateinit var observer: FileObserver
    
    var files = mutableStateOf(listOf<File>())
    var filesDir: String = ""
    
    fun init() {
        files.value = File(filesDir).listFiles {
            _, name -> name!!.endsWith(".kt")
        }?.toList() ?: listOf()
        observer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = File(filesDir)
            object : FileObserver(file) {
                override fun onEvent(event: Int, file: String?) {
                    if (event == CREATE || event == DELETE || event == MODIFY) {
                        updateFileList()
                    }
                }
            }
        } else {
            object : FileObserver(filesDir) {
                override fun onEvent(event: Int, file: String?) {
                    if (event == CREATE || event == DELETE || event == MODIFY) {
                        updateFileList()
                    }
                }
            }
        }
        observer.startWatching()
    }
    
    fun updateFileList() {
        CoroutineScope(Dispatchers.Main).run {
            iles.value = (File(filesDir).listFiles {
                _, name -> name!!.endsWith(".kt")
            }?.toList() ?: listOf()).sortedBy {
                it.lastModified()
            }.reversed()
        }
    }
}