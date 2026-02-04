package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleSyncAction : SubmoduleAction() {
    override val id: String = "submodule.sync"
    override val name: String = "Sync Submodules"
    override val description: String = "Synchronize submodule remote URL configuration"
    override val icon: String = "git_submodule_sync"

    override val defaultKeybinding: Keybinding? = null

    var recursive: Boolean = true

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        val args = mutableListOf("submodule", "sync")
        if (recursive) {
            args.add("--recursive")
        }

        val result = executeGitCommand(projectPath, *args.toTypedArray())

        return if (result.success) {
            SubmoduleActionContext.onSubmoduleSyncRequested?.invoke(projectPath)
            ActionResult.Success("Submodules synchronized successfully", result.output)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to sync submodules: ${result.output}")
        }
    }
}
