package com.scto.codelikebastimove.feature.explorer

import java.io.File

data class FileItem(
  val file: File,
  val name: String = file.name,
  val path: String = file.absolutePath,
  val isDirectory: Boolean = file.isDirectory,
  val size: Long = if (file.isFile) file.length() else 0L,
  val lastModified: Long = file.lastModified(),
  val extension: String = file.extension,
  val isHidden: Boolean = file.isHidden,
) {
  val isAndroidProject: Boolean
    get() =
      isDirectory &&
        (File(file, "build.gradle.kts").exists() || File(file, "build.gradle").exists())

  val isGradleProject: Boolean
    get() =
      isDirectory &&
        (File(file, "settings.gradle.kts").exists() ||
          File(file, "settings.gradle").exists() ||
          File(file, "build.gradle.kts").exists() ||
          File(file, "build.gradle").exists())

  val displaySize: String
    get() =
      when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
      }
}

enum class SortOrder {
  NAME_ASC,
  NAME_DESC,
  DATE_ASC,
  DATE_DESC,
  SIZE_ASC,
  SIZE_DESC,
}

enum class FileFilter {
  ALL,
  DIRECTORIES_ONLY,
  FILES_ONLY,
  PROJECTS_ONLY,
}
