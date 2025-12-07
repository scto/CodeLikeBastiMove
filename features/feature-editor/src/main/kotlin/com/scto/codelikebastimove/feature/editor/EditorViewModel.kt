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
    val fileContent: String = "",
    val openTabs: List<String> = emptyList(),
    val fileContents: Map<String, String> = emptyMap()
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
            val content = _state.value.fileContents[path] ?: file.content
            _state.value = _state.value.copy(
                selectedFile = file,
                selectedFilePath = path,
                fileContent = content
            )
        } else {
            _state.value = _state.value.copy(
                selectedFilePath = path
            )
        }
    }
    
    fun openFileInTab(path: String) {
        val currentTabs = _state.value.openTabs.toMutableList()
        if (!currentTabs.contains(path)) {
            currentTabs.add(path)
            _state.value = _state.value.copy(openTabs = currentTabs)
        }
    }
    
    fun closeTab(path: String) {
        val currentTabs = _state.value.openTabs.toMutableList()
        currentTabs.remove(path)
        
        val newState = _state.value.copy(openTabs = currentTabs)
        
        if (_state.value.selectedFilePath == path) {
            val newSelectedPath = currentTabs.lastOrNull()
            if (newSelectedPath != null) {
                selectFile(newSelectedPath)
                _state.value = _state.value.copy(openTabs = currentTabs)
            } else {
                _state.value = newState.copy(
                    selectedFile = null,
                    selectedFilePath = null,
                    fileContent = ""
                )
            }
        } else {
            _state.value = newState
        }
    }

    fun updateFileContent(content: String) {
        val path = _state.value.selectedFilePath ?: return
        val currentContents = _state.value.fileContents.toMutableMap()
        currentContents[path] = content
        _state.value = _state.value.copy(
            fileContent = content,
            fileContents = currentContents
        )
    }
}
