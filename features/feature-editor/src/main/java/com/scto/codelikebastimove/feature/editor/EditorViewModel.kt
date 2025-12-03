package com.scto.codelikebastimove.feature.editor

import androidx.lifecycle.ViewModel

import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectFile
import com.scto.codelikebastimove.feature.treeview.TreeNodeData
import com.scto.codelikebastimove.feature.treeview.TreeViewUtils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EditorState(
    val project: Project? = null,
    val treeNodes: List<TreeNodeData> = emptyList(),
    val selectedFile: ProjectFile? = null,
    val selectedFilePath: String? = null,
    val fileContent: String = ""
)

class EditorViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    fun setProject(project: Project) {
        val treeNodes = TreeViewUtils.buildTreeFromProjectFiles(
            files = project.files,
            projectName = project.name
        )
        _state.value = EditorState(
            project = project,
            treeNodes = treeNodes
        )
    }

    fun selectFile(path: String) {
        val project = _state.value.project ?: return
        val file = project.files.find { it.relativePath == path }
        
        if (file != null && !file.isDirectory) {
            _state.value = _state.value.copy(
                selectedFile = file,
                selectedFilePath = path,
                fileContent = file.content
            )
        } else {
            _state.value = _state.value.copy(
                selectedFilePath = path
            )
        }
    }

    fun updateFileContent(content: String) {
        _state.value = _state.value.copy(fileContent = content)
    }
}