package com.scto.codelikebastimove.feature.soraeditor.lsp

import com.scto.codelikebastimove.feature.soraeditor.command.EditorActionContext
import com.scto.codelikebastimove.feature.soraeditor.widget.SoraEditorView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object LspContext {
    private val _connector = MutableStateFlow<BaseLspConnector?>(null)
    val connector: StateFlow<BaseLspConnector?> = _connector.asStateFlow()

    private val connectors = mutableMapOf<String, BaseLspConnector>()

    private var currentFilePath: String? = null
    private var currentLanguageId: String? = null

    var onGoToLocation: ((LspLocation) -> Unit)? = null
    var onShowReferences: ((List<LspLocation>) -> Unit)? = null
    var onShowRenameDialog: ((String, (String) -> Unit) -> Unit)? = null
    var onShowError: ((String) -> Unit)? = null
    var onShowMessage: ((String) -> Unit)? = null

    init {
        EditorActionContext.addFocusListener { editor ->
            if (editor == null) {
                clearActiveContext()
            }
        }
    }

    fun setConnector(connector: BaseLspConnector?) {
        _connector.value = connector
    }

    fun registerConnector(languageId: String, connector: BaseLspConnector) {
        connectors[languageId.lowercase()] = connector
    }

    fun unregisterConnector(languageId: String) {
        connectors.remove(languageId.lowercase())
    }

    fun getConnectorForLanguage(languageId: String): BaseLspConnector? {
        return connectors[languageId.lowercase()]
    }

    fun getCurrentConnector(): BaseLspConnector? = _connector.value

    fun hasActiveConnector(): Boolean = _connector.value != null

    fun setActiveContext(filePath: String, languageId: String) {
        currentFilePath = filePath
        currentLanguageId = languageId
        val connector = getConnectorForLanguage(languageId)
        setConnector(connector)
    }

    fun clearActiveContext() {
        currentFilePath = null
        currentLanguageId = null
    }

    fun getCurrentFilePath(): String? = currentFilePath

    fun getCurrentLanguageId(): String? = currentLanguageId

    fun pathToUri(path: String): String {
        return if (path.startsWith("file://")) {
            path
        } else {
            "file://${File(path).absolutePath}"
        }
    }

    fun uriToPath(uri: String): String {
        return if (uri.startsWith("file://")) {
            uri.removePrefix("file://")
        } else {
            uri
        }
    }

    fun notifyGoToLocation(location: LspLocation) {
        onGoToLocation?.invoke(location)
    }

    fun notifyShowReferences(locations: List<LspLocation>) {
        onShowReferences?.invoke(locations)
    }

    fun showRenameDialog(currentName: String, onRename: (String) -> Unit) {
        onShowRenameDialog?.invoke(currentName, onRename)
    }

    fun showError(message: String) {
        onShowError?.invoke(message)
    }

    fun showMessage(message: String) {
        onShowMessage?.invoke(message)
    }
}

data class LspActionContext(
    val editor: SoraEditorView,
    val uri: String,
    val connector: BaseLspConnector,
)
