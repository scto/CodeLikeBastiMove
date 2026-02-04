package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleInitAction : SubmoduleAction() {
    override val id: String = "submodule.init"
    override val name: String = "Initialize Submodules"
    override val description: String = "Initialize all submodules in the repository"
    override val icon: String = "git_submodule_init"

    override val defaultKeybinding: Keybinding? = null

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        val result = executeGitCommand(projectPath, "submodule", "init")

        return if (result.success) {
            SubmoduleActionContext.onSubmoduleInitRequested?.invoke(projectPath)
            ActionResult.Success("Submodules initialized successfully", result.output)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to initialize submodules: ${result.output}")
        }
    }
}
