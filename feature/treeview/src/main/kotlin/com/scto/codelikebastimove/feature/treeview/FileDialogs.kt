package com.scto.codelikebastimove.feature.treeview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private val DialogBackground = Color(0xFF1E1E1E)
private val DialogText = Color(0xFFE0E0E0)
private val DialogBorder = Color(0xFF3C3C3C)
private val AccentColor = Color(0xFF0D6EFD)
private val DangerColor = Color(0xFFDC3545)

@Composable
fun CreateFileDialog(
  isVisible: Boolean,
  isFolder: Boolean,
  parentPath: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (!isVisible) return

  var fileName by remember { mutableStateOf("") }
  var error by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = DialogBackground),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = if (isFolder) "Create New Folder" else "Create New File",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = DialogText,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = fileName,
          onValueChange = {
            fileName = it
            error = validateFileName(it)
          },
          label = { Text(if (isFolder) "Folder name" else "File name") },
          placeholder = { Text(if (isFolder) "my-folder" else "MyFile.kt") },
          singleLine = true,
          isError = error != null,
          supportingText = error?.let { { Text(it, color = DangerColor) } },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentColor,
            unfocusedBorderColor = DialogBorder,
            focusedTextColor = DialogText,
            unfocusedTextColor = DialogText,
            cursorColor = AccentColor,
          ),
          modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.align(Alignment.End)) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = {
              if (error == null && fileName.isNotBlank()) {
                onConfirm(fileName)
              }
            },
            enabled = error == null && fileName.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
          ) {
            Text("Create")
          }
        }
      }
    }
  }
}

@Composable
fun RenameDialog(
  isVisible: Boolean,
  currentName: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  if (!isVisible) return

  var newName by remember(currentName) { mutableStateOf(currentName) }
  var error by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = DialogBackground),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = "Rename",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = DialogText,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = newName,
          onValueChange = {
            newName = it
            error = validateFileName(it)
          },
          label = { Text("New name") },
          singleLine = true,
          isError = error != null,
          supportingText = error?.let { { Text(it, color = DangerColor) } },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentColor,
            unfocusedBorderColor = DialogBorder,
            focusedTextColor = DialogText,
            unfocusedTextColor = DialogText,
            cursorColor = AccentColor,
          ),
          modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.align(Alignment.End)) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = {
              if (error == null && newName.isNotBlank() && newName != currentName) {
                onConfirm(newName)
              }
            },
            enabled = error == null && newName.isNotBlank() && newName != currentName,
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
          ) {
            Text("Rename")
          }
        }
      }
    }
  }
}

@Composable
fun DeleteConfirmDialog(
  isVisible: Boolean,
  fileName: String,
  isDirectory: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  if (!isVisible) return

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = DialogBackground),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Text(
          text = if (isDirectory) "Delete Folder?" else "Delete File?",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = DialogText,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = if (isDirectory) {
            "Are you sure you want to delete \"$fileName\" and all its contents? This action cannot be undone."
          } else {
            "Are you sure you want to delete \"$fileName\"? This action cannot be undone."
          },
          style = MaterialTheme.typography.bodyMedium,
          color = DialogText.copy(alpha = 0.8f),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.align(Alignment.End)) {
          OutlinedButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(12.dp))

          Button(
            onClick = {
              onConfirm()
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
          ) {
            Text("Delete")
          }
        }
      }
    }
  }
}

private fun validateFileName(name: String): String? {
  if (name.isBlank()) return null
  if (name.contains("/") || name.contains("\\")) return "Name cannot contain slashes"
  if (name.contains(":")) return "Name cannot contain colons"
  if (name.length > 255) return "Name is too long"
  val invalidChars = listOf('<', '>', '"', '|', '?', '*')
  if (invalidChars.any { name.contains(it) }) return "Name contains invalid characters"
  return null
}
