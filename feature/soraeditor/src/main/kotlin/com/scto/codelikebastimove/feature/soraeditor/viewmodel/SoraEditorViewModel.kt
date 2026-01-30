package com.scto.codelikebastimove.feature.soraeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.soraeditor.model.EditorConfig
import com.scto.codelikebastimove.feature.soraeditor.model.EditorFile
import com.scto.codelikebastimove.feature.soraeditor.model.EditorLanguageType
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTab
import com.scto.codelikebastimove.feature.soraeditor.model.EditorTheme
import com.scto.codelikebastimove.feature.soraeditor.model.EditorThemes
import com.scto.codelikebastimove.feature.soraeditor.model.HighlightingMode
import java.io.File
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SoraEditorViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(SoraEditorUiState())
  val uiState: StateFlow<SoraEditorUiState> = _uiState.asStateFlow()

  private val fileContents = mutableMapOf<String, String>()

  fun openFile(filePath: String) {
    viewModelScope.launch {
      try {
        val file = File(filePath)
        if (!file.exists()) return@launch

        val existingTab = _uiState.value.tabs.find { it.file.path == filePath }
        if (existingTab != null) {
          selectTab(existingTab.id)
          return@launch
        }

        val content = file.readText()
        val languageType = EditorLanguageType.fromFileName(file.name)

        val editorFile =
          EditorFile(
            path = filePath,
            name = file.name,
            content = content,
            languageType = languageType,
          )

        val tabId = UUID.randomUUID().toString()
        val newTab = EditorTab(id = tabId, file = editorFile, isActive = true)

        fileContents[tabId] = content

        _uiState.update { state ->
          state.copy(
            tabs = state.tabs.map { it.copy(isActive = false) } + newTab,
            activeTabId = tabId,
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun createNewFile(name: String = "untitled.txt") {
    val languageType = EditorLanguageType.fromFileName(name)
    val editorFile = EditorFile(path = "", name = name, content = "", languageType = languageType)

    val tabId = UUID.randomUUID().toString()
    val newTab = EditorTab(id = tabId, file = editorFile, isActive = true)

    fileContents[tabId] = ""

    _uiState.update { state ->
      state.copy(tabs = state.tabs.map { it.copy(isActive = false) } + newTab, activeTabId = tabId)
    }
  }

  fun selectTab(tabId: String) {
    _uiState.update { state ->
      state.copy(
        tabs = state.tabs.map { tab -> tab.copy(isActive = tab.id == tabId) },
        activeTabId = tabId,
      )
    }
  }

  fun closeTab(tabId: String) {
    _uiState.update { state ->
      val remainingTabs = state.tabs.filter { it.id != tabId }
      val newActiveTabId =
        if (state.activeTabId == tabId) {
          remainingTabs.lastOrNull()?.id
        } else {
          state.activeTabId
        }

      fileContents.remove(tabId)

      state.copy(
        tabs = remainingTabs.map { tab -> tab.copy(isActive = tab.id == newActiveTabId) },
        activeTabId = newActiveTabId,
      )
    }
  }

  fun updateContent(tabId: String, content: String) {
    val originalContent = fileContents[tabId]
    val isModified = originalContent != content

    _uiState.update { state ->
      state.copy(
        tabs =
          state.tabs.map { tab ->
            if (tab.id == tabId) {
              tab.copy(file = tab.file.copy(content = content, isModified = isModified))
            } else {
              tab
            }
          }
      )
    }
  }

  fun saveFile(tabId: String) {
    viewModelScope.launch {
      val tab = _uiState.value.tabs.find { it.id == tabId } ?: return@launch

      try {
        if (tab.file.path.isNotEmpty()) {
          val file = File(tab.file.path)
          file.writeText(tab.file.content)

          fileContents[tabId] = tab.file.content

          _uiState.update { state ->
            state.copy(
              tabs =
                state.tabs.map { t ->
                  if (t.id == tabId) {
                    t.copy(file = t.file.copy(isModified = false))
                  } else {
                    t
                  }
                }
            )
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun saveAllFiles() {
    _uiState.value.tabs.filter { it.file.isModified }.forEach { tab -> saveFile(tab.id) }
  }

  fun updateConfig(config: EditorConfig) {
    _uiState.update { it.copy(config = config) }
  }

  fun updateTheme(theme: EditorTheme) {
    _uiState.update { it.copy(theme = theme) }
  }

  fun setHighlightingMode(mode: HighlightingMode) {
    _uiState.update { state -> state.copy(config = state.config.copy(highlightingMode = mode)) }
  }

  fun updateTextSize(size: Float) {
    _uiState.update { state -> state.copy(config = state.config.copy(textSize = size)) }
  }

  fun toggleLineNumbers() {
    _uiState.update { state ->
      state.copy(config = state.config.copy(showLineNumbers = !state.config.showLineNumbers))
    }
  }

  fun toggleWordWrap() {
    _uiState.update { state ->
      state.copy(config = state.config.copy(wordWrap = !state.config.wordWrap))
    }
  }

  fun hasUnsavedChanges(): Boolean {
    return _uiState.value.tabs.any { it.file.isModified }
  }

  fun getUnsavedTabs(): List<EditorTab> {
    return _uiState.value.tabs.filter { it.file.isModified }
  }
}

data class SoraEditorUiState(
  val tabs: List<EditorTab> = emptyList(),
  val activeTabId: String? = null,
  val config: EditorConfig = EditorConfig(),
  val theme: EditorTheme = EditorThemes.DarkModern,
  val isLoading: Boolean = false,
  val error: String? = null,
)
