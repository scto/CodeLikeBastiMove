package com.scto.codelikebastimove.feature.main.assetstudio

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.codelikebastimove.feature.main.assetstudio.converter.SvgToAvdConverter
import com.scto.codelikebastimove.feature.main.assetstudio.model.AVDDocument
import com.scto.codelikebastimove.feature.main.assetstudio.model.ExportConfig
import com.scto.codelikebastimove.feature.main.assetstudio.model.ExportFormat
import com.scto.codelikebastimove.feature.main.assetstudio.model.IconCategory
import com.scto.codelikebastimove.feature.main.assetstudio.model.IconProvider
import com.scto.codelikebastimove.feature.main.assetstudio.model.IconStyle
import com.scto.codelikebastimove.feature.main.assetstudio.model.VectorAsset
import com.scto.codelikebastimove.feature.main.assetstudio.model.VectorGroup
import com.scto.codelikebastimove.feature.main.assetstudio.model.VectorPath
import com.scto.codelikebastimove.feature.main.assetstudio.repository.IconRepositoryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class VectorAssetStudioState(
    val currentTab: AssetStudioTab = AssetStudioTab.BROWSE,
    val searchQuery: String = "",
    val selectedProvider: IconProvider = IconProvider.MATERIAL_ICONS,
    val selectedCategory: String? = null,
    val selectedStyle: IconStyle = IconStyle.FILLED,
    val viewMode: ViewMode = ViewMode.GRID,
    val icons: List<VectorAsset> = emptyList(),
    val categories: List<IconCategory> = emptyList(),
    val featuredIcons: List<VectorAsset> = emptyList(),
    val selectedIcons: Set<String> = emptySet(),
    val selectedAsset: VectorAsset? = null,
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val currentPage: Int = 0,
    val currentDocument: AVDDocument? = null,
    val selectedPath: VectorPath? = null,
    val exportConfig: ExportConfig = ExportConfig(format = ExportFormat.AVD_XML),
    val showExportDialog: Boolean = false,
    val showColorPicker: Boolean = false,
    val previewColor: Color = Color.Black,
    val previewScale: Float = 1f,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val svgImportContent: String = "",
    val showSvgImportDialog: Boolean = false,
    val generatedCode: String = ""
)

enum class AssetStudioTab(val title: String) {
    BROWSE("Browse"),
    CREATE("Create"),
    EDIT("Edit"),
    CONVERT("Convert")
}

enum class ViewMode {
    GRID, LIST
}

class VectorAssetStudioViewModel : ViewModel() {

    private val repositoryManager = IconRepositoryManager()
    private val converter = SvgToAvdConverter()

    private val _uiState = MutableStateFlow(VectorAssetStudioState())
    val uiState: StateFlow<VectorAssetStudioState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val repository = repositoryManager.getCurrentRepository()
            
            val categories = repository.getCategories()
            val featured = repository.getFeaturedIcons()
            val searchResult = repository.searchIcons("")
            
            _uiState.update {
                it.copy(
                    categories = categories,
                    featuredIcons = featured,
                    icons = searchResult.icons,
                    hasMore = searchResult.hasMore,
                    isLoading = false
                )
            }
        }
    }

    fun setTab(tab: AssetStudioTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchIcons()
    }

    fun setProvider(provider: IconProvider) {
        repositoryManager.setProvider(provider)
        _uiState.update { 
            it.copy(
                selectedProvider = provider,
                icons = emptyList(),
                categories = emptyList(),
                currentPage = 0
            )
        }
        loadInitialData()
    }

    fun setCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category, currentPage = 0) }
        searchIcons()
    }

    fun setStyle(style: IconStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
        searchIcons()
    }

    fun setViewMode(mode: ViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun toggleIconSelection(iconId: String) {
        _uiState.update { state ->
            val newSelection = if (iconId in state.selectedIcons) {
                state.selectedIcons - iconId
            } else {
                state.selectedIcons + iconId
            }
            state.copy(selectedIcons = newSelection)
        }
    }

    fun selectAllIcons() {
        _uiState.update { state ->
            state.copy(selectedIcons = state.icons.map { it.id }.toSet())
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedIcons = emptySet()) }
    }

    fun selectAsset(asset: VectorAsset?) {
        _uiState.update { it.copy(selectedAsset = asset) }
        if (asset != null) {
            openAssetForEditing(asset)
        }
    }

    private fun searchIcons() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val state = _uiState.value
            val repository = repositoryManager.getCurrentRepository()
            
            val result = repository.searchIcons(
                query = state.searchQuery,
                category = state.selectedCategory,
                style = state.selectedStyle,
                page = 0
            )
            
            _uiState.update {
                it.copy(
                    icons = result.icons,
                    hasMore = result.hasMore,
                    currentPage = 0,
                    isLoading = false
                )
            }
        }
    }

    fun loadMoreIcons() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val repository = repositoryManager.getCurrentRepository()
            val nextPage = state.currentPage + 1
            
            val result = repository.searchIcons(
                query = state.searchQuery,
                category = state.selectedCategory,
                style = state.selectedStyle,
                page = nextPage
            )
            
            _uiState.update {
                it.copy(
                    icons = it.icons + result.icons,
                    hasMore = result.hasMore,
                    currentPage = nextPage,
                    isLoading = false
                )
            }
        }
    }

    fun createNewAvd(name: String = "new_icon") {
        val document = AVDDocument(
            name = name,
            rootGroup = VectorGroup(
                id = "root",
                paths = listOf(
                    VectorPath(
                        id = UUID.randomUUID().toString(),
                        pathData = "M12,2L2,7l10,5l10,-5L12,2z",
                        fillColor = Color.Black
                    )
                )
            )
        )
        
        _uiState.update { 
            it.copy(
                currentDocument = document,
                currentTab = AssetStudioTab.EDIT
            )
        }
    }

    fun openAssetForEditing(asset: VectorAsset) {
        val document = AVDDocument(
            name = asset.name,
            rootGroup = VectorGroup(
                id = "root",
                paths = listOf(
                    VectorPath(
                        id = UUID.randomUUID().toString(),
                        pathData = "M12,2C6.48,2,2,6.48,2,12s4.48,10,10,10s10,-4.48,10,-10S17.52,2,12,2z",
                        fillColor = asset.previewColor
                    )
                )
            )
        )
        
        _uiState.update { 
            it.copy(
                currentDocument = document,
                currentTab = AssetStudioTab.EDIT
            )
        }
    }

    fun updatePath(pathId: String, updatedPath: VectorPath) {
        _uiState.update { state ->
            val document = state.currentDocument ?: return@update state
            val updatedGroup = updatePathInGroup(document.rootGroup, pathId, updatedPath)
            state.copy(currentDocument = document.copy(rootGroup = updatedGroup))
        }
    }

    private fun updatePathInGroup(group: VectorGroup, pathId: String, updatedPath: VectorPath): VectorGroup {
        val updatedPaths = group.paths.map { if (it.id == pathId) updatedPath else it }
        val updatedGroups = group.groups.map { updatePathInGroup(it, pathId, updatedPath) }
        return group.copy(paths = updatedPaths, groups = updatedGroups)
    }

    fun addPath(pathData: String, fillColor: Color = Color.Black) {
        _uiState.update { state ->
            val document = state.currentDocument ?: return@update state
            val newPath = VectorPath(
                id = UUID.randomUUID().toString(),
                pathData = pathData,
                fillColor = fillColor
            )
            val updatedGroup = document.rootGroup.copy(
                paths = document.rootGroup.paths + newPath
            )
            state.copy(currentDocument = document.copy(rootGroup = updatedGroup))
        }
    }

    fun removePath(pathId: String) {
        _uiState.update { state ->
            val document = state.currentDocument ?: return@update state
            val updatedGroup = removePathFromGroup(document.rootGroup, pathId)
            state.copy(currentDocument = document.copy(rootGroup = updatedGroup))
        }
    }

    private fun removePathFromGroup(group: VectorGroup, pathId: String): VectorGroup {
        val updatedPaths = group.paths.filter { it.id != pathId }
        val updatedGroups = group.groups.map { removePathFromGroup(it, pathId) }
        return group.copy(paths = updatedPaths, groups = updatedGroups)
    }

    fun selectPath(path: VectorPath?) {
        _uiState.update { it.copy(selectedPath = path) }
    }

    fun setPreviewColor(color: Color) {
        _uiState.update { it.copy(previewColor = color) }
    }

    fun setPreviewScale(scale: Float) {
        _uiState.update { it.copy(previewScale = scale.coerceIn(0.5f, 4f)) }
    }

    fun showSvgImportDialog() {
        _uiState.update { it.copy(showSvgImportDialog = true) }
    }

    fun hideSvgImportDialog() {
        _uiState.update { it.copy(showSvgImportDialog = false, svgImportContent = "") }
    }

    fun setSvgImportContent(content: String) {
        _uiState.update { it.copy(svgImportContent = content) }
    }

    fun importSvg() {
        val svgContent = _uiState.value.svgImportContent
        if (svgContent.isBlank()) {
            _uiState.update { it.copy(errorMessage = "SVG content is empty") }
            return
        }
        
        val result = converter.convertSvgToAvd(svgContent)
        result.onSuccess { document ->
            _uiState.update { 
                it.copy(
                    currentDocument = document,
                    currentTab = AssetStudioTab.EDIT,
                    showSvgImportDialog = false,
                    svgImportContent = "",
                    successMessage = "SVG imported successfully"
                )
            }
        }.onFailure { error ->
            _uiState.update { it.copy(errorMessage = "Failed to import SVG: ${error.message}") }
        }
    }

    fun showExportDialog() {
        _uiState.update { it.copy(showExportDialog = true) }
    }

    fun hideExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }

    fun setExportFormat(format: ExportFormat) {
        _uiState.update { it.copy(exportConfig = it.exportConfig.copy(format = format)) }
    }

    fun setExportSize(size: Int) {
        _uiState.update { it.copy(exportConfig = it.exportConfig.copy(size = size)) }
    }

    fun setExportColor(color: Color) {
        _uiState.update { it.copy(exportConfig = it.exportConfig.copy(color = color)) }
    }

    fun exportCurrentDocument(): String? {
        val document = _uiState.value.currentDocument ?: return null
        val config = _uiState.value.exportConfig
        
        return when (config.format) {
            ExportFormat.AVD_XML -> converter.convertAvdToXml(document)
            ExportFormat.SVG -> converter.convertAvdToSvg(document)
            ExportFormat.COMPOSE_ICON -> converter.convertAvdToComposeImageVector(document)
            else -> converter.convertAvdToXml(document)
        }
    }

    fun exportSelectedIcons() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val selectedIds = _uiState.value.selectedIcons
            val icons = _uiState.value.icons.filter { it.id in selectedIds }
            val repository = repositoryManager.getCurrentRepository()
            
            val exportedCodes = mutableListOf<String>()
            for (icon in icons) {
                repository.downloadIcon(icon).onSuccess { code ->
                    exportedCodes.add("<!-- ${icon.name} -->\n$code")
                }
            }
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    generatedCode = exportedCodes.joinToString("\n\n"),
                    successMessage = "Exported ${exportedCodes.size} icons"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun getAvailableProviders(): List<IconProvider> = repositoryManager.getAvailableProviders()
}
