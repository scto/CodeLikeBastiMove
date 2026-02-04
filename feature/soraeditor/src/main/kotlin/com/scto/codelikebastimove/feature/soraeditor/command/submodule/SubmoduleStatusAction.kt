package com.scto.codelikebastimove.feature.soraeditor.command.submodule

import com.scto.codelikebastimove.core.actions.api.action.ActionContext
import com.scto.codelikebastimove.core.actions.api.action.ActionResult
import com.scto.codelikebastimove.core.actions.api.action.Keybinding

class SubmoduleStatusAction : SubmoduleAction() {
    override val id: String = "submodule.status"
    override val name: String = "Submodule Status"
    override val description: String = "Show the status of all submodules"
    override val icon: String = "git_submodule_status"

    override val defaultKeybinding: Keybinding? = null

    override suspend fun executeSubmodule(projectPath: String, context: ActionContext): ActionResult {
        if (!isGitRepository(projectPath)) {
            return ActionResult.Failure("Not a Git repository")
        }

        val result = executeGitCommand(projectPath, "submodule", "status", "--recursive")

        return if (result.success) {
            val submodules = parseSubmoduleStatus(result.output)
            SubmoduleActionContext.onSubmoduleListReceived?.invoke(submodules)
            SubmoduleActionContext.onSubmoduleStatusRequested?.invoke(projectPath)
            ActionResult.Success("Submodule status retrieved", submodules)
        } else {
            SubmoduleActionContext.onSubmoduleError?.invoke(result.output)
            ActionResult.Failure("Failed to get submodule status: ${result.output}")
        }
    }

    private fun parseSubmoduleStatus(output: String): List<SubmoduleInfo> {
        if (output.isBlank()) return emptyList()

        return output.lines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                parseStatusLine(line)
            }
    }

    private fun parseStatusLine(line: String): SubmoduleInfo? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return null

        val status = when {
            trimmed.startsWith("-") -> SubmoduleStatus.UNINITIALIZED
            trimmed.startsWith("+") -> SubmoduleStatus.MODIFIED
            trimmed.startsWith("U") -> SubmoduleStatus.CONFLICT
            else -> SubmoduleStatus.INITIALIZED
        }

        val parts = trimmed.removePrefix("-").removePrefix("+").removePrefix("U").trim().split(" ", limit = 2)
        if (parts.size < 2) return null

        val commitHash = parts[0].take(40)
        val pathAndBranch = parts[1]

        val (path, branch) = if (pathAndBranch.contains(" (")) {
            val idx = pathAndBranch.lastIndexOf(" (")
            val p = pathAndBranch.substring(0, idx)
            val b = pathAndBranch.substring(idx + 2).removeSuffix(")")
            p to b
        } else {
            pathAndBranch to null
        }

        return SubmoduleInfo(
            name = path.substringAfterLast("/"),
            path = path,
            url = "",
            branch = branch,
            status = status,
            commitHash = commitHash,
        )
    }
}
