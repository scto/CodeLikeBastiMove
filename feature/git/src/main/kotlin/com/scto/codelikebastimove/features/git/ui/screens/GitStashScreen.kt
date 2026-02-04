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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.scto.codelikebastimove.feature.git.model.GitStash
import com.scto.codelikebastimove.feature.git.viewmodel.GitViewModel

private val GitSurfaceBackground = Color(0xFF1A1A1A)
private val GitCardBackground = Color(0xFF2A2A2A)
private val GitAccentColor = Color(0xFF8B7355)
private val GitTextColor = Color(0xFFE0D4C8)

@Composable
fun GitStashContent(
    stashes: List<GitStash>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onStash: (String?) -> Unit,
    onStashPop: (Int) -> Unit,
    onStashApply: (Int) -> Unit,
    onStashDrop: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showNewStashDialog by remember { mutableStateOf(false) }
    var stashMessage by remember { mutableStateOf("") }

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
                                text = stringResource(R.string.stash_management),
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
                            text = "${stashes.size} stash(es)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GitTextColor.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            if (stashes.isEmpty()) {
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
                                Icons.Default.Archive,
                                contentDescription = null,
                                tint = GitTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.height(48.dp),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_stashes),
                                color = GitTextColor.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }

            items(stashes) { stash ->
                StashCard(
                    stash = stash,
                    onPop = { onStashPop(stash.index) },
                    onApply = { onStashApply(stash.index) },
                    onDrop = { onStashDrop(stash.index) },
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showNewStashDialog = true },
            containerColor = GitAccentColor,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.stash_changes))
        }
    }

    if (showNewStashDialog) {
        AlertDialog(
            onDismissRequest = { showNewStashDialog = false },
            containerColor = GitCardBackground,
            title = { Text(stringResource(R.string.stash_changes), color = GitTextColor) },
            text = {
                OutlinedTextField(
                    value = stashMessage,
                    onValueChange = { stashMessage = it },
                    label = { Text(stringResource(R.string.message_optional)) },
                    placeholder = { Text("WIP: feature implementation") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GitTextColor,
                        unfocusedTextColor = GitTextColor,
                        focusedBorderColor = GitAccentColor,
                        unfocusedBorderColor = Color(0xFF444444),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStash(stashMessage.takeIf { it.isNotBlank() })
                        stashMessage = ""
                        showNewStashDialog = false
                    }
                ) {
                    Text(stringResource(R.string.stash))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewStashDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun StashCard(
    stash: GitStash,
    onPop: () -> Unit,
    onApply: () -> Unit,
    onDrop: () -> Unit,
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
                Text(
                    text = "stash@{${stash.index}}",
                    style = MaterialTheme.typography.titleSmall,
                    color = GitAccentColor,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stash.branch,
                    style = MaterialTheme.typography.bodySmall,
                    color = GitTextColor.copy(alpha = 0.7f),
                )
            }

            Text(
                text = stash.message,
                style = MaterialTheme.typography.bodyMedium,
                color = GitTextColor,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onPop,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = GitTextColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.pop), color = GitTextColor)
                }

                OutlinedButton(
                    onClick = onApply,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(stringResource(R.string.apply), color = GitTextColor)
                }

                OutlinedButton(
                    onClick = onDrop,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.drop), color = Color(0xFFEF4444))
                }
            }
        }
    }
}
