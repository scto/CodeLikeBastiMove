package com.termux.shared.shell

interface ShellEnvironmentClient {
  fun getDefaultWorkingDirectoryPath(): String

  fun getDefaultBinPath(): String

  fun isRootShell(): Boolean

  fun setWorkingDirectory(path: String)

  fun getEnvironment(): Map<String, String>

  fun buildEnvironment(): Array<String>
}

data class ShellEnvironment(
  val workingDirectory: String,
  val binPath: String,
  val homePath: String,
  val tmpPath: String,
  val prefixPath: String,
  val env: Map<String, String>,
) {
  fun toEnvironmentArray(): Array<String> {
    return env.map { (key, value) -> "$key=$value" }.toTypedArray()
  }
}

class DefaultShellEnvironmentClient(private val environment: ShellEnvironment) :
  ShellEnvironmentClient {

  private var currentWorkingDirectory: String = environment.workingDirectory

  override fun getDefaultWorkingDirectoryPath(): String = currentWorkingDirectory

  override fun getDefaultBinPath(): String = environment.binPath

  override fun isRootShell(): Boolean = false

  override fun setWorkingDirectory(path: String) {
    currentWorkingDirectory = path
  }

  override fun getEnvironment(): Map<String, String> = environment.env

  override fun buildEnvironment(): Array<String> = environment.toEnvironmentArray()
}
