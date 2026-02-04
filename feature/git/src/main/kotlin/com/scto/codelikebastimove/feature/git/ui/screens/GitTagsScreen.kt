package com.scto.codelikebastimove.feature.git.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.git.model.GitTag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val GitSurfaceBackground = Color(0xFF1A1A1A)
private val GitCardBackground = Color(0xFF2A2A2A)
private val GitAccentColor = Color(0xFF8B7355)
private val GitTextColor = Color(0xFFE0D4C8)
private val GitTagColor = Color(0xFF4A9F7A)

@Composable
fun GitTagsContent(
    tags: List<GitTag>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCreateTag: (name: String, message: String?) -> Unit,
    onDeleteTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showNewTagDialog by remember { mutableStateOf(false) }
    var tagName by remember { mutableStateOf("") }
    var tagMessage by remember { mutableStateOf("") }
    var isAnnotated by remember { mutableStateOf(true) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GitCardBackground),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.tag_management),
                                style = MaterialTheme.typography.titleMedium,
                                color = GitTextColor,
                                fontWeight = FontWeight.SemiBold,
                            )
                            IconButton(onClick = onRefresh) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = stringResource(R.string.refresh),
                                    tint = GitTextColor,
                                )
                            }
                        }

                        Text(
                            text = "${tags.size} tag(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GitTextColor.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            if (tags.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GitCardBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = GitTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.height(48.dp),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_tags),
                                color = GitTextColor.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }

            items(tags) { tag ->
                TagCard(
                    tag = tag,
                    dateFormat = dateFormat,
                    onDelete = { onDeleteTag(tag.name) },
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showNewTagDialog = true },
            containerColor = GitAccentColor,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.create_tag))
        }
    }

    if (showNewTagDialog) {
        AlertDialog(
            onDismissRequest = { showNewTagDialog = false },
            containerColor = GitCardBackground,
            title = { Text(stringResource(R.string.create_tag), color = GitTextColor) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        label = { Text(stringResource(R.string.tag_name)) },
                        placeholder = { Text("v1.0.0") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GitTextColor,
                            unfocusedTextColor = GitTextColor,
                            focusedBorderColor = GitAccentColor,
                            unfocusedBorderColor = Color(0xFF444444),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = isAnnotated,
                            onCheckedChange = { isAnnotated = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GitAccentColor,
                            ),
                        )
                        Text(
                            text = stringResource(R.string.annotated_tag),
                            color = GitTextColor,
                        )
                    }

                    if (isAnnotated) {
                        OutlinedTextField(
                            value = tagMessage,
                            onValueChange = { tagMessage = it },
                            label = { Text(stringResource(R.string.tag_message)) },
                            placeholder = { Text("Release version 1.0.0") },
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GitTextColor,
                                unfocusedTextColor = GitTextColor,
                                focusedBorderColor = GitAccentColor,
                                unfocusedBorderColor = Color(0xFF444444),
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tagName.isNotBlank()) {
                            onCreateTag(
                                tagName,
                                if (isAnnotated) tagMessage.takeIf { it.isNotBlank() } else null,
                            )
                            tagName = ""
                            tagMessage = ""
                            showNewTagDialog = false
                        }
                    },
                    enabled = tagName.isNotBlank(),
                ) {
                    Text(stringResource(R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTagDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun TagCard(
    tag: GitTag,
    dateFormat: SimpleDateFormat,
    onDelete: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GitCardBackground),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GitTagColor,
                    ) {
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }

                    if (tag.isAnnotated) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GitAccentColor.copy(alpha = 0.3f),
                        ) {
                            Text(
                                text = "annotated",
                                style = MaterialTheme.typography.labelSmall,
                                color = GitTextColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Color(0xFFEF4444),
                    )
                }
            }

            if (!tag.message.isNullOrBlank()) {
                Text(
                    text = tag.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GitTextColor,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = tag.commitHash.take(7),
                    style = MaterialTheme.typography.bodySmall,
                    color = GitTagColor,
                )

                tag.date?.let { date ->
                    Text(
                        text = dateFormat.format(Date(date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = GitTextColor.copy(alpha = 0.7f),
                    )
                }

                tag.taggerName?.let { tagger ->
                    Text(
                        text = tagger,
                        style = MaterialTheme.typography.bodySmall,
                        color = GitTextColor.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
