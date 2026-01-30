package com.scto.codelikebastimove.core.templates.api

data class ProjectFile(
  val relativePath: String,
  val content: String,
  val isDirectory: Boolean = false,
)
