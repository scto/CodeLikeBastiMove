package com.scto.codelikebastimove.feature.git.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.git.model.DiffLineType
import com.scto.codelikebastimove.feature.git.model.GitDiff
import com.scto.codelikebastimove.feature.git.model.GitDiffHunk
import com.scto.codelikebastimove.feature.git.model.GitDiffLine

private val GitSurfaceBackground = Color(0xFF1A1A1A)
private val GitCardBackground = Color(0xFF2A2A2A)
private val GitTextColor = Color(0xFFE0D4C8)
private val DiffAdditionBackground = Color(0xFF1E3A2F)
private val DiffAdditionText = Color(0xFF4ADE80)
private val DiffDeletionBackground = Color(0xFF3A1E1E)
private val DiffDeletionText = Color(0xFFEF4444)
private val DiffContextBackground = Color(0xFF2A2A2A)
private val DiffLineNumberColor = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitDiffScreen(
    diff: GitDiff?,
    fileName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = GitSurfaceBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.diff_viewer),
                            color = GitTextColor,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = fileName,
                            color = GitTextColor.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = GitTextColor,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GitSurfaceBackground,
                ),
            )
        },
        modifier = modifier,
    ) { padding ->
        if (diff == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_changes),
                    color = GitTextColor.copy(alpha = 0.5f),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(diff.hunks) { hunk ->
                    DiffHunkCard(hunk = hunk)
                }
            }
        }
    }
}

@Composable
private fun DiffHunkCard(hunk: GitDiffHunk) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GitCardBackground,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Surface(
                color = Color(0xFF3A3A3A),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "@@ -${hunk.oldStart},${hunk.oldCount} +${hunk.newStart},${hunk.newCount} @@",
                    color = Color(0xFF9CA3AF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                hunk.lines.forEach { line ->
                    DiffLineRow(line = line)
                }
            }
        }
    }
}

@Composable
private fun DiffLineRow(line: GitDiffLine) {
    val (backgroundColor, textColor, icon) = when (line.type) {
        DiffLineType.ADDITION -> Triple(DiffAdditionBackground, DiffAdditionText, Icons.Default.Add)
        DiffLineType.DELETION -> Triple(DiffDeletionBackground, DiffDeletionText, Icons.Default.Remove)
        DiffLineType.CONTEXT -> Triple(DiffContextBackground, GitTextColor, null)
        DiffLineType.HEADER -> Triple(Color(0xFF3A3A3A), Color(0xFF9CA3AF), null)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = (line.oldLineNumber?.toString() ?: "").padStart(4),
            color = DiffLineNumberColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.width(40.dp).padding(start = 4.dp),
        )

        Text(
            text = (line.newLineNumber?.toString() ?: "").padStart(4),
            color = DiffLineNumberColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.width(40.dp),
        )

        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.height(14.dp),
                )
            }
        }

        Text(
            text = line.content,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}
