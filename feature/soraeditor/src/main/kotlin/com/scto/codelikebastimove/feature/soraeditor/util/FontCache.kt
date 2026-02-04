package com.scto.codelikebastimove.feature.soraeditor.util

import android.content.Context
import android.graphics.Typeface
import java.io.File

object FontCache {
    private val cachedFonts = mutableMapOf<String, Typeface>()

    fun loadFont(context: Context, path: String, isAsset: Boolean) {
        runCatching {
            val font = if (isAsset) {
                context.assets.open(path).close()
                Typeface.createFromAsset(context.assets, path)
            } else {
                val file = File(path)
                if (!file.exists()) {
                    return
                }
                Typeface.createFromFile(file)
            }
            cachedFonts[path] = font
        }.onFailure { it.printStackTrace() }
    }

    fun getFont(context: Context, path: String, isAsset: Boolean): Typeface? {
        return if (cachedFonts.containsKey(path)) {
            cachedFonts[path]
        } else {
            runCatching {
                val font = if (isAsset) {
                    Typeface.createFromAsset(context.assets, path)
                } else {
                    Typeface.createFromFile(File(path))
                }
                cachedFonts[path] = font
                font
            }.getOrNull()
        }
    }

    fun preloadFonts(context: Context, assetPaths: List<String>) {
        assetPaths.forEach { path ->
            loadFont(context, path, isAsset = true)
        }
    }

    fun clearCache() {
        cachedFonts.clear()
    }

    fun isFontCached(path: String): Boolean = cachedFonts.containsKey(path)
}
