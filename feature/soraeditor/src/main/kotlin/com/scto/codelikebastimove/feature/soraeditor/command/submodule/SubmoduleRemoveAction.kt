package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding
import java.io.File

class SubmoduleRemoveAction : SubmoduleAction() {
    override val id: String = "submodule.remove"
    override val name: String = "Remove Submodule"
    override val description: String = "Remove a submodule from the repository"
    override val icon: String = "git_submodule_remove"

    override val defaultKeybinding: Keybinding? = null

    var submodulePath: String = ""

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        if (submodulePath.isBlank()) {
            return ActionResult.Failure("Submodule path is required")
        }

        val deinitResult = executeGitCommand(projectPath, "submodule", "deinit", "-f", submodulePath)
        if (!deinitResult.success) {
            SubmoduleActionContext.onSubmoduleError?.invoke(deinitResult.output)
            return ActionResult.Failure("Failed to deinitialize submodule: ${deinitResult.output}")
        }

        val removeResult = executeGitCommand(projectPath, "rm", "-f", submodulePath)
        if (!removeResult.success) {
            SubmoduleActionContext.onSubmoduleError?.invoke(removeResult.output)
            return ActionResult.Failure("Failed to remove submodule: ${removeResult.output}")
        }

        val gitModulesPath = File(projectPath, ".git/modules/$submodulePath")
        if (gitModulesPath.exists()) {
            gitModulesPath.deleteRecursively()
        }

        SubmoduleActionContext.onSubmoduleRemoveRequested?.invoke(projectPath, submodulePath)
        return ActionResult.Success("Submodule removed successfully: $submodulePath")
    }

    fun configure(path: String): SubmoduleRemoveAction {
        submodulePath = path
        return this
    }
}
