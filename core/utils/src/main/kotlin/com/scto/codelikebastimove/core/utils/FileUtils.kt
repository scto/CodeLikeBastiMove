package com.scto.codelikebastimove.core.utils

import com.scto.codelikebastimove.core.logger.CLBMLogger
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileUtils {

    private const val TAG = "FileUtils"

    suspend fun readFileContent(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(FileNotFoundException("File not found: ${file.path}"))
            }
            if (!file.isFile) {
                return@withContext Result.failure(IllegalArgumentException("Path is not a file: ${file.path}"))
            }
            Result.success(file.readText())
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Error reading file: ${file.path}", e)
            Result.failure(e)
        }
    }

    suspend fun writeFileContent(file: File, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            file.parentFile?.mkdirs()
            file.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Error writing file: ${file.path}", e)
            Result.failure(e)
        }
    }

    suspend fun createFile(parentDir: File, fileName: String, content: String = ""): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                val newFile = File(parentDir, fileName)
                if (newFile.exists()) {
                    return@withContext Result.failure(FileAlreadyExistsException(newFile, reason = "File already exists"))
                }
                newFile.writeText(content)
                Result.success(newFile)
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Error creating file: $fileName in ${parentDir.path}", e)
                Result.failure(e)
            }
        }

    suspend fun deleteFile(file: File): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val deleted = if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
            if (deleted) {
                CLBMLogger.d(TAG, "Deleted: ${file.path}")
            }
            Result.success(deleted)
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Error deleting: ${file.path}", e)
            Result.failure(e)
        }
    }

    suspend fun renameFile(file: File, newName: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val newFile = File(file.parentFile, newName)
            if (newFile.exists()) {
                return@withContext Result.failure(FileAlreadyExistsException(newFile, reason = "Target already exists"))
            }
            val success = file.renameTo(newFile)
            if (success) {
                Result.success(newFile)
            } else {
                Result.failure(Exception("Failed to rename file"))
            }
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Error renaming file: ${file.path} to $newName", e)
            Result.failure(e)
        }
    }

    suspend fun copyFile(source: File, targetDir: File, newName: String? = null): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
                val targetFile = File(targetDir, newName ?: source.name)
                if (targetFile.exists()) {
                    return@withContext Result.failure(FileAlreadyExistsException(targetFile, reason = "Target already exists"))
                }
                if (source.isDirectory) {
                    source.copyRecursively(targetFile, overwrite = false)
                } else {
                    source.copyTo(targetFile, overwrite = false)
                }
                Result.success(targetFile)
            } catch (e: Exception) {
                CLBMLogger.e(TAG, "Error copying file: ${source.path}", e)
                Result.failure(e)
            }
        }

    suspend fun moveFile(source: File, targetDir: File, newName: String? = null): Result<File> =
        withContext(Dispatchers.IO) {
            copyFile(source, targetDir, newName).mapCatching { targetFile ->
                deleteFile(source)
                targetFile
            }
        }

    suspend fun createFolder(parentDir: File, folderName: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val newFolder = File(parentDir, folderName)
            if (newFolder.exists()) {
                return@withContext Result.failure(FileAlreadyExistsException(newFolder, reason = "Folder already exists"))
            }
            val created = newFolder.mkdirs()
            if (created) {
                Result.success(newFolder)
            } else {
                Result.failure(Exception("Failed to create folder"))
            }
        } catch (e: Exception) {
            CLBMLogger.e(TAG, "Error creating folder: $folderName in ${parentDir.path}", e)
            Result.failure(e)
        }
    }

    fun getFileExtension(file: File): String = file.extension

    fun getFileNameWithoutExtension(file: File): String = file.nameWithoutExtension

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
}

class FileNotFoundException(message: String) : Exception(message)
