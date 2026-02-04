package com.scto.codelikebastimove.core.projectmanager.model

import com.scto.codelikebastimove.core.datastore.ProjectTemplateType

data class ProjectInfo(
    val name: String,
    val path: String,
    val packageName: String,
    val templateType: ProjectTemplateType,
    val minSdk: Int,
    val useKotlin: Boolean,
    val useKotlinDsl: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),
)

data class ProjectCreationOptions(
    val name: String,
    val directory: String,
    val packageName: String,
    val templateType: ProjectTemplateType,
    val minSdk: Int,
    val useKotlin: Boolean = true,
    val useKotlinDsl: Boolean = true,
)
