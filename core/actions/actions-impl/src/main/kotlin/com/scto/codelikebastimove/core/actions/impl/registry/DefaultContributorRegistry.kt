package com.scto.codelikebastimove.core.actions.impl.registry

import com.scto.codelikebastimove.core.actions.api.contribution.*
import com.scto.codelikebastimove.core.actions.api.keybinding.ResolvedKeybinding
import java.util.concurrent.ConcurrentHashMap

class DefaultContributorRegistry : ContributorRegistry {

  private val contributors = ConcurrentHashMap<String, ActionContributor>()

  override fun registerContributor(contributor: ActionContributor) {
    contributors[contributor.contributorId] = contributor
  }

  override fun unregisterContributor(contributorId: String) {
    contributors.remove(contributorId)
  }

  override fun getContributors(): List<ActionContributor> {
    return contributors.values.toList()
  }

  override fun getContributor(contributorId: String): ActionContributor? {
    return contributors[contributorId]
  }

  override fun getAllActionContributions(): List<EditorActionContribution> {
    return contributors.values.flatMap { it.getActionContributions() }
  }

  override fun getAllCommandContributions(): List<CommandContribution> {
    return contributors.values.flatMap { it.getCommandContributions() }
  }

  override fun getAllKeybindingContributions(): List<ResolvedKeybinding> {
    return contributors.values.flatMap { it.getKeybindingContributions() }
  }

  override fun getMenuContributions(menuId: String): List<MenuItemContribution> {
    return contributors.values
      .flatMap { it.getMenuContributions()[menuId] ?: emptyList() }
      .sortedBy { it.order }
  }
}
