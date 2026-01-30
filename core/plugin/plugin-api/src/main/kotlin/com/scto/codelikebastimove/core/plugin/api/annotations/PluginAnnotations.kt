package com.scto.codelikebastimove.core.plugin.api.annotations

import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginCategory
import com.scto.codelikebastimove.core.plugin.api.descriptor.PluginPermission

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PluginInfo(
  val id: String,
  val name: String,
  val version: String,
  val description: String = "",
  val author: String = "",
  val minHostVersion: String = "1.0.0",
  val category: PluginCategory = PluginCategory.GENERAL,
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class RequiresPermission(val permission: PluginPermission)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class DependsOn(
  val pluginId: String,
  val version: String = "",
  val optional: Boolean = false,
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionContribution(val extensionPointId: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EditorActionContribution(
  val id: String,
  val name: String,
  val description: String = "",
  val icon: String = "",
  val shortcut: String = "",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToolWindowContribution(
  val id: String,
  val name: String,
  val description: String = "",
  val icon: String = "",
  val position: String = "BOTTOM",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandContribution(
  val id: String,
  val title: String,
  val category: String = "General",
  val icon: String = "",
  val shortcut: String = "",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ThemeContribution(
  val id: String,
  val name: String,
  val description: String = "",
  val isDark: Boolean = false,
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FileTypeContribution(
  val id: String,
  val name: String,
  val extensions: Array<String>,
  val icon: String = "",
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProjectTemplateContribution(
  val id: String,
  val name: String,
  val description: String = "",
  val category: String = "General",
  val icon: String = "",
)
