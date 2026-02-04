package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.KeyModifier
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleUpdateAction : SubmoduleAction() {
    override val id: String = "submodule.update"
    override val name: String = "Update Submodules"
    override val description: String = "Update all submodules to their tracked commits"
    override val icon: String = "git_submodule_update"

    override val defaultKeybinding: Keybinding = Keybinding("U", setOf(KeyModifier.CTRL, KeyModifier.SHIFT, KeyModifier.ALT))

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        val result = executeGitCommand(projectPath, "submodule", "update", "--init", "--recursive")

        return if (result.success) {
            SubmoduleActionContext.onSubmoduleUpdateRequested?.invoke(projectPath)
            ActionResult.Success("Submodules updated successfully", result.output)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to update submodules: ${result.output}")
        }
    }
}
