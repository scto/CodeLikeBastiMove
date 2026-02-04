package com.scto.codelikebastimove.feature.treeview

import java.io.File

sealed class FileOperationResult {
  data class Success(val file: File) : FileOperationResult()
  data class Error(val message: String) : FileOperationResult()
}

interface FileOperations {
  fun createFile(parentPath: String, fileName: String): FileOperationResult
  fun createFolder(parentPath: String, folderName: String): FileOperationResult
  fun rename(filePath: String, newName: String): FileOperationResult
  fun delete(filePath: String): FileOperationResult
  fun copy(sourcePath: String, destinationPath: String): FileOperationResult
  fun move(sourcePath: String, destinationPath: String): FileOperationResult
  fun exists(path: String): Boolean
  fun isDirectory(path: String): Boolean
}

class DefaultFileOperations : FileOperations {

  override fun createFile(parentPath: String, fileName: String): FileOperationResult {
    return try {
      val parentDir = File(parentPath)
      if (!parentDir.exists() || !parentDir.isDirectory) {
        return FileOperationResult.Error("Parent directory does not exist")
      }

      val newFile = File(parentDir, fileName)
      if (newFile.exists()) {
        return FileOperationResult.Error("File already exists")
      }

      if (newFile.createNewFile()) {
        FileOperationResult.Success(newFile)
      } else {
        FileOperationResult.Error("Failed to create file")
      }
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun createFolder(parentPath: String, folderName: String): FileOperationResult {
    return try {
      val parentDir = File(parentPath)
      if (!parentDir.exists() || !parentDir.isDirectory) {
        return FileOperationResult.Error("Parent directory does not exist")
      }

      val newFolder = File(parentDir, folderName)
      if (newFolder.exists()) {
        return FileOperationResult.Error("Folder already exists")
      }

      if (newFolder.mkdir()) {
        FileOperationResult.Success(newFolder)
      } else {
        FileOperationResult.Error("Failed to create folder")
      }
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun rename(filePath: String, newName: String): FileOperationResult {
    return try {
      val file = File(filePath)
      if (!file.exists()) {
        return FileOperationResult.Error("File does not exist")
      }

      val newFile = File(file.parentFile, newName)
      if (newFile.exists()) {
        return FileOperationResult.Error("A file with that name already exists")
      }

      if (file.renameTo(newFile)) {
        FileOperationResult.Success(newFile)
      } else {
        FileOperationResult.Error("Failed to rename file")
      }
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun delete(filePath: String): FileOperationResult {
    return try {
      val file = File(filePath)
      if (!file.exists()) {
        return FileOperationResult.Error("File does not exist")
      }

      if (file.isDirectory) {
        if (file.deleteRecursively()) {
          FileOperationResult.Success(file)
        } else {
          FileOperationResult.Error("Failed to delete folder")
        }
      } else {
        if (file.delete()) {
          FileOperationResult.Success(file)
        } else {
          FileOperationResult.Error("Failed to delete file")
        }
      }
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun copy(sourcePath: String, destinationPath: String): FileOperationResult {
    return try {
      val source = File(sourcePath)
      val destination = File(destinationPath)

      if (!source.exists()) {
        return FileOperationResult.Error("Source file does not exist")
      }

      if (destination.exists()) {
        return FileOperationResult.Error("Destination already exists")
      }

      if (source.isDirectory) {
        source.copyRecursively(destination, overwrite = false)
      } else {
        source.copyTo(destination, overwrite = false)
      }

      FileOperationResult.Success(destination)
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun move(sourcePath: String, destinationPath: String): FileOperationResult {
    return try {
      val source = File(sourcePath)
      val destination = File(destinationPath)

      if (!source.exists()) {
        return FileOperationResult.Error("Source file does not exist")
      }

      if (destination.exists()) {
        return FileOperationResult.Error("Destination already exists")
      }

      if (source.renameTo(destination)) {
        FileOperationResult.Success(destination)
      } else {
        val copyResult = copy(sourcePath, destinationPath)
        if (copyResult is FileOperationResult.Success) {
          delete(sourcePath)
          FileOperationResult.Success(destination)
        } else {
          copyResult
        }
      }
    } catch (e: Exception) {
      FileOperationResult.Error(e.message ?: "Unknown error")
    }
  }

  override fun exists(path: String): Boolean {
    return File(path).exists()
  }

  override fun isDirectory(path: String): Boolean {
    return File(path).isDirectory
  }
}
