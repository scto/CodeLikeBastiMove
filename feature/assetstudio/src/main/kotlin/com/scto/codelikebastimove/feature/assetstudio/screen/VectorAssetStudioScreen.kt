package com.scto.codelikebastimove.feature.assetstudio.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.core.ui.components.AdaptiveTopAppBar
import com.scto.codelikebastimove.feature.assetstudio.AssetStudioTab
import com.scto.codelikebastimove.feature.assetstudio.VectorAssetStudioState
import com.scto.codelikebastimove.feature.assetstudio.VectorAssetStudioViewModel
import com.scto.codelikebastimove.feature.assetstudio.ViewMode
import com.scto.codelikebastimove.feature.assetstudio.model.ExportConfig
import com.scto.codelikebastimove.feature.assetstudio.model.ExportFormat
import com.scto.codelikebastimove.feature.assetstudio.model.VectorAsset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VectorAssetStudioScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: VectorAssetStudioViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
    uiState.successMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearMessages()
    }
    uiState.errorMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearMessages()
    }
  }

  Scaffold(
    topBar = {
      AdaptiveTopAppBar(
        title = stringResource(R.string.vector_asset_studio),
        navigationIcon = {
          IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.close)) }
        },
        actions = {
          if (uiState.selectedIcons.isNotEmpty()) {
            IconButton(onClick = { viewModel.exportSelectedIcons() }) {
              Icon(Icons.Default.Download, "Export Selected")
            }
            IconButton(onClick = { viewModel.clearSelection() }) {
              Icon(Icons.Default.Close, "Clear Selection")
            }
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      if (uiState.currentTab == AssetStudioTab.BROWSE && uiState.selectedIcons.isNotEmpty()) {
        FloatingActionButton(onClick = { viewModel.showExportDialog() }) {
          Icon(Icons.Default.FileDownload, "Export")
        }
      }
    },
    modifier = modifier,
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      ScrollableTabRow(
        selectedTabIndex = AssetStudioTab.entries.indexOf(uiState.currentTab),
        edgePadding = 16.dp,
      ) {
        AssetStudioTab.entries.forEach { tab ->
          Tab(
            selected = uiState.currentTab == tab,
            onClick = { viewModel.setTab(tab) },
            text = { Text(tab.title) },
          )
        }
      }

      when (uiState.currentTab) {
        AssetStudioTab.BROWSE -> BrowseTab(viewModel, uiState)
        AssetStudioTab.CREATE -> CreateTab(viewModel, uiState)
        AssetStudioTab.EDIT -> EditTab(viewModel, uiState)
        AssetStudioTab.CONVERT -> ConvertTab(viewModel, uiState, context)
      }
    }
  }

  if (uiState.showExportDialog) {
    ExportDialog(
      exportConfig = uiState.exportConfig,
      selectedCount = uiState.selectedIcons.size,
      onFormatChange = { viewModel.setExportFormat(it) },
      onSizeChange = { viewModel.setExportSize(it) },
      onExport = {
        viewModel.exportSelectedIcons()
        viewModel.hideExportDialog()
      },
      onDismiss = { viewModel.hideExportDialog() },
    )
  }

  if (uiState.showSvgImportDialog) {
    SvgImportDialog(
      svgContent = uiState.svgImportContent,
      onContentChange = { viewModel.setSvgImportContent(it) },
      onImport = { viewModel.importSvg() },
      onDismiss = { viewModel.hideSvgImportDialog() },
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BrowseTab(viewModel: VectorAssetStudioViewModel, state: VectorAssetStudioState) {
  var showProviderDropdown by remember { mutableStateOf(false) }
  var showCategoryFilter by remember { mutableStateOf(false) }

  Column(modifier = Modifier.fillMaxSize()) {
    Surface(
      color = MaterialTheme.colorScheme.surfaceContainer,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box {
            FilledTonalButton(onClick = { showProviderDropdown = true }) {
              Text(state.selectedProvider.displayName)
            }
            DropdownMenu(
              expanded = showProviderDropdown,
              onDismissRequest = { showProviderDropdown = false },
            ) {
              viewModel.getAvailableProviders().forEach { provider ->
                DropdownMenuItem(
                  text = { Text(provider.displayName) },
                  onClick = {
                    viewModel.setProvider(provider)
                    showProviderDropdown = false
                  },
                  leadingIcon = {
                    if (provider == state.selectedProvider) {
                      Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                  },
                )
              }
            }
          }

          Row {
            IconButton(onClick = { showCategoryFilter = !showCategoryFilter }) {
              Icon(Icons.Default.FilterList, "Filter")
            }
            IconButton(
              onClick = {
                viewModel.setViewMode(
                  if (state.viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
                )
              }
            ) {
              Icon(
                if (state.viewMode == ViewMode.GRID) Icons.AutoMirrored.Filled.ViewList
                else Icons.Default.GridView,
                "Toggle View",
              )
            }
            if (state.icons.isNotEmpty()) {
              IconButton(onClick = { viewModel.selectAllIcons() }) {
                Icon(Icons.Default.SelectAll, "Select All")
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = state.searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search icons...") },
          leadingIcon = { Icon(Icons.Default.Search, null) },
          trailingIcon = {
            if (state.searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Close, "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
        )

        AnimatedVisibility(visible = showCategoryFilter) {
          Column {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "Categories",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              FilterChip(
                selected = state.selectedCategory == null,
                onClick = { viewModel.setCategory(null) },
                label = { Text("All") },
              )
              state.categories.forEach { category ->
                FilterChip(
                  selected = state.selectedCategory == category.id,
                  onClick = { viewModel.setCategory(category.id) },
                  label = { Text("${category.name} (${category.iconCount})") },
                )
              }
            }
          }
        }
      }
    }

    if (state.selectedIcons.isNotEmpty()) {
      Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            "${state.selectedIcons.size} selected",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          Spacer(modifier = Modifier.weight(1f))
          TextButton(onClick = { viewModel.clearSelection() }) { Text("Clear") }
        }
      }
    }

    if (state.isLoading && state.icons.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    } else if (state.icons.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.Image,
            null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            "No icons found",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    } else {
      if (state.viewMode == ViewMode.GRID) {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(80.dp),
          contentPadding = PaddingValues(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxSize(),
        ) {
          items(state.icons, key = { it.id }) { icon ->
            IconGridItem(
              icon = icon,
              isSelected = icon.id in state.selectedIcons,
              onClick = { viewModel.selectAsset(icon) },
              onLongClick = { viewModel.toggleIconSelection(icon.id) },
              onSelectionToggle = { viewModel.toggleIconSelection(icon.id) },
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxSize(),
        ) {
          items(state.icons, key = { it.id }) { icon ->
            IconListItem(
              icon = icon,
              isSelected = icon.id in state.selectedIcons,
              onClick = { viewModel.selectAsset(icon) },
              onSelectionToggle = { viewModel.toggleIconSelection(icon.id) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun IconGridItem(
  icon: VectorAsset,
  isSelected: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  onSelectionToggle: () -> Unit,
) {
  Card(
    onClick = onClick,
    modifier =
      Modifier.aspectRatio(1f)
        .then(
          if (isSelected) {
            Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
          } else Modifier
        ),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
          } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
          }
      ),
    shape = RoundedCornerShape(12.dp),
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
      ) {
        icon.imageVector?.let { vector ->
          Icon(
            imageVector = vector,
            contentDescription = icon.name,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
          ?: Box(
            modifier =
              Modifier.size(32.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Text(icon.name.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          icon.name,
          style = MaterialTheme.typography.labelSmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center,
        )
      }

      if (isSelected) {
        Box(
          modifier =
            Modifier.align(Alignment.TopEnd)
              .padding(4.dp)
              .size(20.dp)
              .background(MaterialTheme.colorScheme.primary, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            Icons.Default.Check,
            null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onPrimary,
          )
        }
      }
    }
  }
}

@Composable
private fun IconListItem(
  icon: VectorAsset,
  isSelected: Boolean,
  onClick: () -> Unit,
  onSelectionToggle: () -> Unit,
) {
  Card(
    onClick = onClick,
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
          } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
          }
      ),
    shape = RoundedCornerShape(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Checkbox(checked = isSelected, onCheckedChange = { onSelectionToggle() })
      Spacer(modifier = Modifier.width(12.dp))
      icon.imageVector?.let { vector ->
        Icon(imageVector = vector, contentDescription = icon.name, modifier = Modifier.size(28.dp))
      }
        ?: Box(
          modifier =
            Modifier.size(28.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Text(icon.name.take(2).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(icon.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(
          icon.category,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      AssistChip(
        onClick = onClick,
        label = { Text("Edit") },
        leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)) },
      )
    }
  }
}

@Composable
private fun CreateTab(viewModel: VectorAssetStudioViewModel, state: VectorAssetStudioState) {
  var newIconName by remember { mutableStateOf("") }

  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "Create New Vector Drawable",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = newIconName,
          onValueChange = { newIconName = it },
          label = { Text("Icon Name") },
          placeholder = { Text("ic_my_icon") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { viewModel.createNewAvd(newIconName.ifBlank { "new_icon" }) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
        ) {
          Icon(Icons.Default.Add, null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Create Empty AVD")
        }
      }
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "Import from SVG",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          "Convert SVG files to Android Vector Drawable format",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
          onClick = { viewModel.showSvgImportDialog() },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
        ) {
          Icon(Icons.Default.Image, null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Import SVG")
        }
      }
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "Quick Templates",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          items(listOf("Circle", "Square", "Triangle", "Star", "Heart")) { template ->
            Card(
              onClick = { viewModel.createNewAvd("ic_$template".lowercase()) },
              colors =
                CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Box(
                  modifier =
                    Modifier.size(48.dp)
                      .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp),
                      ),
                  contentAlignment = Alignment.Center,
                ) {
                  Text(template.take(1), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(template, style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EditTab(viewModel: VectorAssetStudioViewModel, state: VectorAssetStudioState) {
  val context = LocalContext.current

  if (state.currentDocument == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          Icons.Default.Edit,
          null,
          modifier = Modifier.size(64.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          "No document open",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          "Create or select an icon to edit",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.setTab(AssetStudioTab.CREATE) }) { Text("Create New") }
      }
    }
  } else {
    Column(modifier = Modifier.fillMaxSize()) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            state.currentDocument.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
          )
          Row {
            IconButton(onClick = { viewModel.setPreviewScale(state.previewScale - 0.25f) }) {
              Icon(Icons.Default.ZoomOut, "Zoom Out")
            }
            Text(
              "${(state.previewScale * 100).toInt()}%",
              style = MaterialTheme.typography.labelMedium,
              modifier = Modifier.align(Alignment.CenterVertically),
            )
            IconButton(onClick = { viewModel.setPreviewScale(state.previewScale + 0.25f) }) {
              Icon(Icons.Default.ZoomIn, "Zoom In")
            }
          }
        }
      }

      Row(modifier = Modifier.weight(1f)) {
        Box(
          modifier =
            Modifier.weight(1f)
              .fillMaxSize()
              .background(MaterialTheme.colorScheme.surfaceContainerLowest),
          contentAlignment = Alignment.Center,
        ) {
          Box(
            modifier =
              Modifier.size((state.currentDocument.width * state.previewScale * 4).dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
          ) {
            Text("Preview", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
          }
        }

        Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          modifier = Modifier.width(240.dp),
        ) {
          Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)
          ) {
            Text(
              "Properties",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Size: ${state.currentDocument.width} x ${state.currentDocument.height} dp")
            Text(
              "Viewport: ${state.currentDocument.viewportWidth} x ${state.currentDocument.viewportHeight}"
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
              "Paths (${state.currentDocument.rootGroup.paths.size})",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            state.currentDocument.rootGroup.paths.forEach { path ->
              Card(
                onClick = { viewModel.selectPath(path) },
                modifier = Modifier.fillMaxWidth(),
                colors =
                  CardDefaults.cardColors(
                    containerColor =
                      if (state.selectedPath?.id == path.id) {
                        MaterialTheme.colorScheme.primaryContainer
                      } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                      }
                  ),
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Box(
                    modifier =
                      Modifier.size(20.dp).background(path.fillColor, RoundedCornerShape(4.dp))
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    "Path ${path.id.take(6)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                  )
                  IconButton(
                    onClick = { viewModel.removePath(path.id) },
                    modifier = Modifier.size(24.dp),
                  ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                  }
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
              onClick = { viewModel.addPath("M0,0 L24,24", Color.Gray) },
              modifier = Modifier.fillMaxWidth(),
            ) {
              Icon(Icons.Default.Add, null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Add Path")
            }
          }
        }
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          OutlinedButton(
            onClick = { viewModel.showExportDialog() },
            modifier = Modifier.weight(1f),
          ) {
            Icon(Icons.Default.Download, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export")
          }
          Button(
            onClick = {
              val code = viewModel.exportCurrentDocument()
              if (code != null) {
                val clipboard =
                  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("AVD", code))
              }
            },
            modifier = Modifier.weight(1f),
          ) {
            Icon(Icons.Default.ContentCopy, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Copy Code")
          }
        }
      }
    }
  }
}

@Composable
private fun ConvertTab(
  viewModel: VectorAssetStudioViewModel,
  state: VectorAssetStudioState,
  context: Context,
) {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "SVG to AVD Converter",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          "Convert SVG files to Android Vector Drawable (AVD) format",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.showSvgImportDialog() }, modifier = Modifier.fillMaxWidth()) {
          Icon(Icons.Default.Image, null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Import SVG File")
        }
      }
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          "Export Options",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExportFormat.entries.forEach { format ->
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.setExportFormat(format) }
                .background(
                  if (state.exportConfig.format == format) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                  } else Color.Transparent
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              if (state.exportConfig.format == format) Icons.Default.Check else Icons.Default.Image,
              null,
              tint =
                if (state.exportConfig.format == format) {
                  MaterialTheme.colorScheme.primary
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(format.displayName, fontWeight = FontWeight.Medium)
              Text(
                ".${format.extension}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }

    if (state.generatedCode.isNotEmpty()) {
      ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              "Generated Code",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
            )
            IconButton(
              onClick = {
                val clipboard =
                  context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(
                  ClipData.newPlainText("Generated Code", state.generatedCode)
                )
              }
            ) {
              Icon(Icons.Default.ContentCopy, "Copy")
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              state.generatedCode,
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.padding(12.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ExportDialog(
  exportConfig: ExportConfig,
  selectedCount: Int,
  onFormatChange: (ExportFormat) -> Unit,
  onSizeChange: (Int) -> Unit,
  onExport: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Export Icons") },
    text = {
      Column {
        Text("Export $selectedCount selected icon(s)")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Format:", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ExportFormat.entries.forEach { format ->
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onFormatChange(format) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              if (exportConfig.format == format) Icons.Default.Check else Icons.Default.Image,
              null,
              modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(format.displayName, style = MaterialTheme.typography.bodyMedium)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Size: ${exportConfig.size}dp", style = MaterialTheme.typography.labelMedium)
        Slider(
          value = exportConfig.size.toFloat(),
          onValueChange = { onSizeChange(it.toInt()) },
          valueRange = 16f..128f,
          steps = 6,
        )
      }
    },
    confirmButton = { Button(onClick = onExport) { Text("Export") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}

@Composable
private fun SvgImportDialog(
  svgContent: String,
  onContentChange: (String) -> Unit,
  onImport: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Import SVG") },
    text = {
      Column {
        Text("Paste your SVG content below:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
          value = svgContent,
          onValueChange = onContentChange,
          placeholder = { Text("<svg>...</svg>") },
          modifier = Modifier.fillMaxWidth().height(200.dp),
          textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        )
      }
    },
    confirmButton = {
      Button(onClick = onImport, enabled = svgContent.isNotBlank()) { Text("Import") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}
