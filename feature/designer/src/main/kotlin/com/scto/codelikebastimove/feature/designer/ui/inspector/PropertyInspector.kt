package com.scto.codelikebastimove.feature.designer.ui.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.feature.designer.data.model.Block
import com.scto.codelikebastimove.feature.designer.data.model.PropertyType

@Composable
fun PropertyInspector(
  selectedBlock: Block?,
  onPropertyChanged: (String, Any?) -> Unit,
  onBlockDeleted: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxHeight()
        .width(280.dp)
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .verticalScroll(rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Properties",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      if (selectedBlock != null) {
        IconButton(onClick = onBlockDeleted, modifier = Modifier.size(32.dp)) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Block",
            tint = MaterialTheme.colorScheme.error,
          )
        }
      }
    }

    HorizontalDivider()

    if (selectedBlock == null) {
      Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "No Selection",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Select a block on the canvas to edit its properties",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          )
        }
      }
    } else {
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = selectedBlock.type.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          Text(
            text = selectedBlock.type.category.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      PropertySection(title = "Size & Position") {
        PropertyRow(
          label = "Width",
          value = "${selectedBlock.width.toInt()}",
          onValueChange = { value ->
            value.toFloatOrNull()?.let { onPropertyChanged("width", it) }
          },
          keyboardType = KeyboardType.Number,
        )
        PropertyRow(
          label = "Height",
          value = "${selectedBlock.height.toInt()}",
          onValueChange = { value ->
            value.toFloatOrNull()?.let { onPropertyChanged("height", it) }
          },
          keyboardType = KeyboardType.Number,
        )
        PropertyRow(
          label = "X Position",
          value = "${selectedBlock.position.x.toInt()}",
          onValueChange = { value ->
            value.toFloatOrNull()?.let { onPropertyChanged("positionX", it) }
          },
          keyboardType = KeyboardType.Number,
        )
        PropertyRow(
          label = "Y Position",
          value = "${selectedBlock.position.y.toInt()}",
          onValueChange = { value ->
            value.toFloatOrNull()?.let { onPropertyChanged("positionY", it) }
          },
          keyboardType = KeyboardType.Number,
        )
      }

      selectedBlock.properties.forEach { (key, property) ->
        when (property.type) {
          PropertyType.STRING -> {
            PropertySection(title = property.displayName) {
              PropertyRow(
                label = "",
                value = property.value as? String ?: "",
                onValueChange = { onPropertyChanged(key, it) },
              )
            }
          }
          PropertyType.BOOLEAN -> {
            PropertySection(title = property.displayName) {
              BooleanPropertyRow(
                label = property.displayName,
                checked = property.value as? Boolean ?: false,
                onCheckedChange = { onPropertyChanged(key, it) },
              )
            }
          }
          PropertyType.ENUM -> {
            PropertySection(title = property.displayName) {
              EnumPropertyRow(
                label = "",
                value = property.value as? String ?: "",
                options = property.options ?: emptyList(),
                onValueChange = { onPropertyChanged(key, it) },
              )
            }
          }
          PropertyType.COLOR -> {
            PropertySection(title = property.displayName) {
              ColorPropertyRow(
                label = "",
                value = property.value as? String,
                onValueChange = { onPropertyChanged(key, it) },
              )
            }
          }
          PropertyType.DIMENSION -> {
            PropertySection(title = property.displayName) {
              PropertyRow(
                label = "",
                value = property.value as? String ?: "0.dp",
                onValueChange = { onPropertyChanged(key, it) },
              )
            }
          }
          else -> {}
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun PropertySection(title: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.primary,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.padding(bottom = 4.dp),
    )
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
      Column(modifier = Modifier.padding(8.dp)) { content() }
    }
  }
}

@Composable
private fun PropertyRow(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  keyboardType: KeyboardType = KeyboardType.Text,
  modifier: Modifier = Modifier,
) {
  var text by remember(value) { mutableStateOf(value) }

  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (label.isNotEmpty()) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(80.dp),
      )
    }
    OutlinedTextField(
      value = text,
      onValueChange = {
        text = it
        onValueChange(it)
      },
      singleLine = true,
      textStyle = MaterialTheme.typography.bodySmall,
      keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
      colors =
        OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        ),
      modifier = Modifier.weight(1f).height(40.dp),
    )
  }
}

@Composable
private fun BooleanPropertyRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}

@Composable
private fun EnumPropertyRow(
  label: String,
  value: String,
  options: List<String>,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (label.isNotEmpty()) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(80.dp),
      )
    }
    Box(modifier = Modifier.weight(1f)) {
      Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
        colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(text = value, style = MaterialTheme.typography.bodySmall)
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
          )
        }
      }
      DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { option ->
          DropdownMenuItem(
            text = { Text(option) },
            onClick = {
              onValueChange(option)
              expanded = false
            },
          )
        }
      }
    }
  }
}

@Composable
private fun ColorPropertyRow(
  label: String,
  value: String?,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var text by remember(value) { mutableStateOf(value ?: "") }

  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (label.isNotEmpty()) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(80.dp),
      )
    }
    Icon(
      imageVector = Icons.Default.ColorLens,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(24.dp).padding(end = 8.dp),
    )
    OutlinedTextField(
      value = text,
      onValueChange = {
        text = it
        onValueChange(it)
      },
      singleLine = true,
      placeholder = { Text("e.g., Color.Red") },
      textStyle = MaterialTheme.typography.bodySmall,
      colors =
        OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        ),
      modifier = Modifier.weight(1f).height(40.dp),
    )
  }
}
