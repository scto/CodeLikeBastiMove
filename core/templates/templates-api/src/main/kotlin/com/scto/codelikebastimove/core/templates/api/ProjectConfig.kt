package com.scto.codelikebastimove.core.templates.api

data class ProjectConfig(
  val projectName: String,
  val packageName: String,
  val minSdk: Int,
  val targetSdk: Int = 34,
  val compileSdk: Int = 34,
  val language: ProjectLanguage,
  val gradleLanguage: GradleLanguage,
)
