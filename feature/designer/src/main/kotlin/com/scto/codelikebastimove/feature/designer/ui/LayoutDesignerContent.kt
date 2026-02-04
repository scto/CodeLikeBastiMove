package com.scto.codelikebastimove.feature.designer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.feature.designer.domain.parser.ComposeParser
import com.scto.codelikebastimove.feature.designer.domain.preview.RenderComposeNode
import com.scto.codelikebastimove.feature.designer.ui.preview.ComposePreviewViewModel
import com.scto.codelikebastimove.feature.designer.ui.preview.PreviewViewMode

@Composable
fun LayoutDesignerContent(
    fileContent: String = "",
    fileName: String = "",
    onContentChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val viewModel: ComposePreviewViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fileContent, fileName) {
        if (fileContent.isNotEmpty()) {
            viewModel.loadFromContent(fileContent, fileName)
        }
    }

    LaunchedEffect(uiState.sourceCode) {
        if (uiState.sourceCode.isNotEmpty() && uiState.sourceCode != fileContent) {
            onContentChange?.invoke(uiState.sourceCode)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        DesignerHeader(
            fileName = uiState.fileName,
            hasComposeContent = uiState.hasComposeContent,
            viewMode = uiState.viewMode,
            onViewModeChange = viewModel::setViewMode,
            onRefresh = {
                if (fileContent.isNotEmpty()) {
                    viewModel.loadFromContent(fileContent, fileName)
                }
            },
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        AnimatedContent(
            targetState = uiState.hasComposeContent,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "DesignerContent",
        ) { hasContent ->
            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.fillMaxSize())
            } else if (!hasContent) {
                NoComposeContentMessage(modifier = Modifier.fillMaxSize())
            } else {
                DesignerContentArea(
                    viewModel = viewModel,
                    onContentChange = onContentChange,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun DesignerHeader(
    fileName: String,
    hasComposeContent: Boolean,
    viewMode: PreviewViewMode,
    onViewModeChange: (PreviewViewMode) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DesignServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Compose Preview",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (fileName.isNotEmpty()) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (hasComposeContent) {
                Row {
                    FilterChip(
                        selected = viewMode == PreviewViewMode.CODE_ONLY,
                        onClick = { onViewModeChange(PreviewViewMode.CODE_ONLY) },
                        label = {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = "Code",
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.height(28.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FilterChip(
                        selected = viewMode == PreviewViewMode.SPLIT,
                        onClick = { onViewModeChange(PreviewViewMode.SPLIT) },
                        label = { Text("Split", fontSize = 11.sp) },
                        modifier = Modifier.height(28.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    FilterChip(
                        selected = viewMode == PreviewViewMode.PREVIEW_ONLY,
                        onClick = { onViewModeChange(PreviewViewMode.PREVIEW_ONLY) },
                        label = {
                            Icon(
                                Icons.Outlined.Visibility,
                                contentDescription = "Preview",
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        modifier = Modifier.height(28.dp),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DesignerContentArea(
    viewModel: ComposePreviewViewModel,
    onContentChange: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = modifier) {
        when (uiState.viewMode) {
            PreviewViewMode.CODE_ONLY -> {
                CodePane(
                    code = uiState.sourceCode,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            PreviewViewMode.PREVIEW_ONLY -> {
                LivePreviewPane(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            PreviewViewMode.SPLIT -> {
                CodePane(
                    code = uiState.sourceCode,
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight(),
                )

                VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                LivePreviewPane(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun CodePane(
    code: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val lines = code.lines()
            lines.forEachIndexed { index, line ->
                Row {
                    Text(
                        text = "${index + 1}".padStart(4),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.width(40.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun LivePreviewPane(
    viewModel: ComposePreviewViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Live Preview",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.weight(1f))

                if (uiState.selectedNode != null) {
                    Text(
                        text = "Selected: ${uiState.selectedNode!!.type.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.95f),
            ) {
                val previewNode = uiState.previewNode

                if (previewNode != null) {
                    RenderComposeNode(
                        node = previewNode,
                        selectedNodeId = uiState.selectedNodeId,
                        onNodeSelected = viewModel::selectNode,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.DesignServices,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Preview will appear here",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Analyzing Compose code...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NoComposeContentMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Compose UI Detected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Open a Kotlin file containing @Composable functions to see a live preview of the UI components.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Supported Components",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Column, Row, Box, Card, Text, Button, TextField, Icon, Image, TopAppBar, NavigationBar, Switch, Checkbox, and more...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}
