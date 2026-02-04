package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.AbstractAction
import com.scto.codelikebastimove.core.actions.api.action.ActionCategory
import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.ActionWhen
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import java.io.File

abstract class SubmoduleAction : AbstractAction() {
    override val category: ActionCategory = ActionCategory.GIT

    abstract val defaultKeybinding: Keybinding?

    open val condition: ActionWhen = ActionWhen()

    abstract suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult

    override suspend fun doExecute(context: ActionContext): ActionResult {
        val projectPath = SubmoduleActionContext.currentProjectPath
            ?: return ActionResult.Failure("No active project")
        return executeSubmodule(projectPath, context)
    }

    override fun canExecute(context: ActionContext): Boolean {
        return SubmoduleActionContext.currentProjectPath != null && isEnabled
    }

    protected fun executeGitCommand(projectPath: String, vararg args: String): ProcessResult {
        return try {
            val process = ProcessBuilder("git", *args)
                .directory(File(projectPath))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            ProcessResult(exitCode == 0, output.trim(), exitCode)
        } catch (e: Exception) {
            ProcessResult(false, e.message ?: "Unknown error", -1)
        }
    }

    protected fun isGitRepository(projectPath: String): Boolean {
        val gitDir = File(projectPath, ".git")
        return gitDir.exists()
    }
}

data class ProcessResult(
    val success: Boolean,
    val output: String,
    val exitCode: Int,
)

object SubmoduleActionContext {
    var currentProjectPath: String? = null
        private set

    var onSubmoduleInitRequested: ((String) -> Unit)? = null
    var onSubmoduleUpdateRequested: ((String) -> Unit)? = null
    var onSubmoduleAddRequested: ((String, String, String?) -> Unit)? = null
    var onSubmoduleRemoveRequested: ((String, String) -> Unit)? = null
    var onSubmoduleSyncRequested: ((String) -> Unit)? = null
    var onSubmoduleStatusRequested: ((String) -> Unit)? = null
    var onSubmoduleListReceived: ((List<SubmoduleInfo>) -> Unit)? = null
    var onSubmoduleError: ((String) -> Unit)? = null

    private val projectListeners = mutableListOf<(String?) -> Unit>()

    fun setActiveProject(projectPath: String?) {
        val previous = currentProjectPath
        currentProjectPath = projectPath
        if (previous != projectPath) {
            projectListeners.forEach { it(projectPath) }
        }
    }

    fun clearProject() {
        setActiveProject(null)
    }

    fun addProjectListener(listener: (String?) -> Unit) {
        projectListeners.add(listener)
    }

    fun removeProjectListener(listener: (String?) -> Unit) {
        projectListeners.remove(listener)
    }

    fun buildActionContext(): ActionContext {
        return ActionContext(
            editorFocus = false,
            editorTextFocus = false,
            inputFocus = false,
            filePath = currentProjectPath,
        )
    }
}

data class SubmoduleInfo(
    val name: String,
    val path: String,
    val url: String,
    val branch: String?,
    val status: SubmoduleStatus,
    val commitHash: String?,
)

enum class SubmoduleStatus {
    INITIALIZED,
    UNINITIALIZED,
    MODIFIED,
    CONFLICT,
    UNKNOWN,
}
