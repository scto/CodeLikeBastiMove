package com.scto.codelikebastimove.core.projectmanager.api

import com.scto.codelikebastimove.core.projectmanager.model.ProjectCreationOptions
import com.scto.codelikebastimove.core.projectmanager.model.ProjectInfo
import kotlinx.coroutines.flow.Flow

interface ProjectManager {
    val recentProjects: Flow<List<ProjectInfo>>

    suspend fun createProject(options: ProjectCreationOptions): Result<ProjectInfo>

    suspend fun openProject(path: String): Result<ProjectInfo>

    suspend fun deleteProject(path: String): Result<Unit>

    suspend fun getProjectInfo(path: String): Result<ProjectInfo>

    suspend fun listProjects(directory: String): Result<List<ProjectInfo>>
}
