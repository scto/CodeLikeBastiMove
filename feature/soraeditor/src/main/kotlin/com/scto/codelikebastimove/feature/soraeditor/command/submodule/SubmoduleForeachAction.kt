package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleForeachAction : SubmoduleAction() {
    override val id: String = "submodule.foreach"
    override val name: String = "Run Command in Submodules"
    override val description: String = "Execute a command in each submodule"
    override val icon: String = "git_submodule_foreach"

    override val defaultKeybinding: Keybinding? = null

    var command: String = ""
    var recursive: Boolean = true

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        if (command.isBlank()) {
            return ActionResult.Failure("Command is required")
        }

        val args = mutableListOf("submodule", "foreach")
        if (recursive) {
            args.add("--recursive")
        }
        args.add(command)

        val result = executeGitCommand(projectPath, *args.toTypedArray())

        return if (result.success) {
            ActionResult.Success("Command executed in all submodules", result.output)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to execute command in submodules: ${result.output}")
        }
    }

    fun configure(cmd: String, isRecursive: Boolean = true): SubmoduleForeachAction {
        command = cmd
        recursive = isRecursive
        return this
    }
}
