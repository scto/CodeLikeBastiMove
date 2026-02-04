package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleAddAction : SubmoduleAction() {
    override val id: String = "submodule.add"
    override val name: String = "Add Submodule"
    override val description: String = "Add a new submodule to the repository"
    override val icon: String = "git_submodule_add"

    override val defaultKeybinding: Keybinding? = null

    var repositoryUrl: String = ""
    var submodulePath: String = ""
    var branch: String? = null

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        if (repositoryUrl.isBlank()) {
            return ActionResult.Failure("Repository URL is required")
        }

        if (submodulePath.isBlank()) {
            return ActionResult.Failure("Submodule path is required")
        }

        val args = mutableListOf("submodule", "add")

        branch?.let {
            args.add("-b")
            args.add(it)
        }

        args.add(repositoryUrl)
        args.add(submodulePath)

        val result = executeGitCommand(projectPath, *args.toTypedArray())

        return if (result.success) {
            SubmoduleActionContext.onSubmoduleAddRequested?.invoke(projectPath, repositoryUrl, submodulePath)
            ActionResult.Success("Submodule added successfully: $submodulePath", result.output)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to add submodule: ${result.output}")
        }
    }

    fun configure(url: String, path: String, branchName: String? = null): SubmoduleAddAction {
        repositoryUrl = url
        submodulePath = path
        branch = branchName
        return this
    }
}
