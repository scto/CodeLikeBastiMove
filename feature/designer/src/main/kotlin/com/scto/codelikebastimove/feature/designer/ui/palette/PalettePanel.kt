package com.scto.codelikebastimove.feature.designer.ui.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.feature.designer.data.model.BlockCategory
import com.scto.codelikebastimove.feature.designer.data.model.BlockType
import com.scto.codelikebastimove.feature.designer.data.model.ComponentDefinition

data class PaletteCategory(
  val category: BlockCategory,
  val icon: ImageVector,
  val displayName: String,
)

val paletteCategories =
  listOf(
    PaletteCategory(BlockCategory.LAYOUT, Icons.Default.ViewColumn, "Layout"),
    PaletteCategory(BlockCategory.CONTAINER, Icons.Default.Category, "Container"),
    PaletteCategory(BlockCategory.TEXT, Icons.Default.TextFields, "Text"),
    PaletteCategory(BlockCategory.INPUT, Icons.Default.Input, "Input"),
    PaletteCategory(BlockCategory.BUTTON, Icons.Default.SmartButton, "Buttons"),
    PaletteCategory(BlockCategory.IMAGE, Icons.Default.Image, "Image"),
    PaletteCategory(BlockCategory.NAVIGATION, Icons.Default.Navigation, "Navigation"),
    PaletteCategory(BlockCategory.CUSTOM, Icons.Default.Extension, "Custom"),
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalettePanel(
  onBlockSelected: (BlockType) -> Unit,
  customComponents: List<ComponentDefinition> = emptyList(),
  onCustomComponentSelected: (ComponentDefinition) -> Unit = {},
  onAddCustomComponent: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  var selectedCategoryIndex by remember { mutableIntStateOf(0) }
  var isGridView by remember { mutableStateOf(true) }

  val selectedCategory = paletteCategories[selectedCategoryIndex].category
  val blocksInCategory = BlockType.entries.filter { it.category == selectedCategory }

  Column(
    modifier =
      modifier.fillMaxHeight().width(240.dp).background(MaterialTheme.colorScheme.surfaceContainer)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Components",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Row {
        IconButton(onClick = { isGridView = true }, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = "Grid View",
            tint =
              if (isGridView) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        IconButton(onClick = { isGridView = false }, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ViewList,
            contentDescription = "List View",
            tint =
              if (!isGridView) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    ScrollableTabRow(
      selectedTabIndex = selectedCategoryIndex,
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
      edgePadding = 8.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      paletteCategories.forEachIndexed { index, category ->
        Tab(
          selected = selectedCategoryIndex == index,
          onClick = { selectedCategoryIndex = index },
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = category.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Text(text = category.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          },
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (selectedCategory == BlockCategory.CUSTOM) {
      Card(
        modifier =
          Modifier.fillMaxWidth().padding(horizontal = 12.dp).clickable { onAddCustomComponent() },
        colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Add Custom Component",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    if (isGridView) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f),
      ) {
        if (selectedCategory == BlockCategory.CUSTOM) {
          items(customComponents) { component ->
            CustomComponentItem(
              component = component,
              onClick = { onCustomComponentSelected(component) },
              isGridView = true,
            )
          }
        } else {
          items(blocksInCategory) { blockType ->
            BlockTypeItem(
              blockType = blockType,
              onClick = { onBlockSelected(blockType) },
              isGridView = true,
            )
          }
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.weight(1f),
      ) {
        if (selectedCategory == BlockCategory.CUSTOM) {
          items(customComponents) { component ->
            CustomComponentItem(
              component = component,
              onClick = { onCustomComponentSelected(component) },
              isGridView = false,
            )
          }
        } else {
          items(blocksInCategory) { blockType ->
            BlockTypeItem(
              blockType = blockType,
              onClick = { onBlockSelected(blockType) },
              isGridView = false,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BlockTypeItem(
  blockType: BlockType,
  onClick: () -> Unit,
  isGridView: Boolean,
  modifier: Modifier = Modifier,
) {
  val icon =
    when (blockType) {
      BlockType.COLUMN -> Icons.Default.ViewColumn
      BlockType.ROW -> Icons.Default.TableRows
      BlockType.BOX -> Icons.Default.Category
      BlockType.TEXT -> Icons.Default.TextFields
      BlockType.BUTTON,
      BlockType.OUTLINED_BUTTON,
      BlockType.TEXT_BUTTON -> Icons.Default.SmartButton
      BlockType.TEXT_FIELD,
      BlockType.OUTLINED_TEXT_FIELD -> Icons.Default.Input
      BlockType.ICON -> Icons.Default.Image
      BlockType.IMAGE -> Icons.Default.Image
      BlockType.TOP_APP_BAR -> Icons.Default.Menu
      BlockType.NAVIGATION_BAR,
      BlockType.BOTTOM_APP_BAR -> Icons.Default.Navigation
      BlockType.SWITCH,
      BlockType.CHECKBOX,
      BlockType.RADIO_BUTTON -> Icons.Default.CheckBox
      else -> Icons.Default.Apps
    }

  if (isGridView) {
    Card(
      modifier = modifier.fillMaxWidth().clickable { onClick() },
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = blockType.displayName,
          style = MaterialTheme.typography.labelSmall,
          textAlign = TextAlign.Center,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  } else {
    Card(
      modifier = modifier.fillMaxWidth().clickable { onClick() },
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.DragIndicator,
          contentDescription = "Drag",
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(24.dp),
          tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = blockType.displayName,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun CustomComponentItem(
  component: ComponentDefinition,
  onClick: () -> Unit,
  isGridView: Boolean,
  modifier: Modifier = Modifier,
) {
  if (isGridView) {
    Card(
      modifier = modifier.fillMaxWidth().clickable { onClick() },
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Icon(
          imageVector = Icons.Default.Extension,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.onTertiaryContainer,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = component.displayName,
          style = MaterialTheme.typography.labelSmall,
          textAlign = TextAlign.Center,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
      }
    }
  } else {
    Card(
      modifier = modifier.fillMaxWidth().clickable { onClick() },
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Default.DragIndicator,
          contentDescription = "Drag",
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.Default.Extension,
          contentDescription = null,
          modifier = Modifier.size(24.dp),
          tint = MaterialTheme.colorScheme.onTertiaryContainer,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = component.displayName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
          )
          if (component.description.isNotEmpty()) {
            Text(
              text = component.description,
              style = MaterialTheme.typography.bodySmall,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
            )
          }
        }
      }
    }
  }
}
