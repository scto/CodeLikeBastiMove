package com.scto.codelikebastimove.feature.git.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.git.viewmodel.GitViewModel
import kotlinx.coroutines.flow.collectLatest

private val GitSurfaceBackground = Color(0xFF1A1A1A)
private val GitCardBackground = Color(0xFF2A2A2A)
private val GitAccentColor = Color(0xFF8B7355)
private val GitTextColor = Color(0xFFE0D4C8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitCloneScreen(
    rootDirectory: String,
    onBackClick: () -> Unit,
    onCloneSuccess: (projectPath: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GitViewModel = viewModel(),
) {
    var repositoryUrl by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var targetDirectory by remember { mutableStateOf("") }
    var shallowClone by remember { mutableStateOf(false) }
    var recursiveSubmodules by remember { mutableStateOf(true) }
    var singleBranch by remember { mutableStateOf(false) }
    var cloneDepth by remember { mutableStateOf("") }

    LaunchedEffect(repositoryUrl, rootDirectory) {
        if (repositoryUrl.isNotBlank() && rootDirectory.isNotBlank()) {
            val repoName = repositoryUrl
                .removeSuffix(".git")
                .removeSuffix("/")
                .substringAfterLast("/")
                .ifBlank { "cloned_repo" }
            targetDirectory = "$rootDirectory/$repoName"
        }
    }

    val isOperationInProgress by viewModel.isOperationInProgress.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.success.collectLatest { message ->
            if (message.startsWith("Repository cloned")) {
                onCloneSuccess(targetDirectory)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GitSurfaceBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.clone_repository),
                        color = GitTextColor,
                        fontWeight = FontWeight.SemiBold,
                    )
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
                modifier = Modifier.statusBarsPadding(),
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE91E63),
                                    Color(0xFF9C27B0),
                                    Color(0xFF3F51B5),
                                    Color(0xFF00BCD4),
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudDownload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Text(
                text = stringResource(R.string.clone_repository),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = GitTextColor,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = repositoryUrl,
                onValueChange = { repositoryUrl = it },
                label = { Text(stringResource(R.string.repository_url), color = GitTextColor.copy(alpha = 0.7f)) },
                placeholder = { Text("https://github.com/user/repo.git", color = Color(0xFF666666)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GitTextColor,
                    unfocusedTextColor = GitTextColor,
                    focusedBorderColor = GitAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = GitAccentColor,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = targetDirectory,
                onValueChange = { targetDirectory = it },
                label = { Text(stringResource(R.string.target_directory), color = GitTextColor.copy(alpha = 0.7f)) },
                placeholder = { Text("/storage/emulated/0/Projects/repo", color = Color(0xFF666666)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GitTextColor,
                    unfocusedTextColor = GitTextColor,
                    focusedBorderColor = GitAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = GitAccentColor,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = branch,
                onValueChange = { branch = it },
                label = { Text(stringResource(R.string.branch_optional), color = GitTextColor.copy(alpha = 0.7f)) },
                placeholder = { Text("main", color = Color(0xFF666666)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = GitTextColor,
                    unfocusedTextColor = GitTextColor,
                    focusedBorderColor = GitAccentColor,
                    unfocusedBorderColor = Color(0xFF444444),
                    cursorColor = GitAccentColor,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.clone_options),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = GitTextColor,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilterChip(
                    selected = recursiveSubmodules,
                    onClick = { recursiveSubmodules = !recursiveSubmodules },
                    label = { Text(stringResource(R.string.recursive_submodules)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GitAccentColor,
                        selectedLabelColor = Color.White,
                        labelColor = GitTextColor,
                    ),
                )

                FilterChip(
                    selected = shallowClone,
                    onClick = { shallowClone = !shallowClone },
                    label = { Text(stringResource(R.string.shallow_clone)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GitAccentColor,
                        selectedLabelColor = Color.White,
                        labelColor = GitTextColor,
                    ),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilterChip(
                    selected = singleBranch,
                    onClick = { singleBranch = !singleBranch },
                    label = { Text(stringResource(R.string.clone_single_branch)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GitAccentColor,
                        selectedLabelColor = Color.White,
                        labelColor = GitTextColor,
                    ),
                )
            }

            if (shallowClone) {
                OutlinedTextField(
                    value = cloneDepth,
                    onValueChange = { cloneDepth = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.clone_depth), color = GitTextColor.copy(alpha = 0.7f)) },
                    placeholder = { Text("1", color = Color(0xFF666666)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GitTextColor,
                        unfocusedTextColor = GitTextColor,
                        focusedBorderColor = GitAccentColor,
                        unfocusedBorderColor = Color(0xFF444444),
                        cursorColor = GitAccentColor,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (isOperationInProgress) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = GitAccentColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.cloning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = GitTextColor.copy(alpha = 0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GitTextColor,
                    ),
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        if (repositoryUrl.isNotBlank() && targetDirectory.isNotBlank()) {
                            viewModel.cloneRepository(
                                url = repositoryUrl,
                                directory = targetDirectory,
                                branch = branch.takeIf { it.isNotBlank() },
                                depth = if (shallowClone) cloneDepth.toIntOrNull() ?: 1 else null,
                                recursive = recursiveSubmodules,
                            )
                        }
                    },
                    enabled = repositoryUrl.isNotBlank() && targetDirectory.isNotBlank() && !isOperationInProgress,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GitAccentColor,
                    ),
                    modifier = Modifier.weight(1f).height(56.dp),
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(stringResource(R.string.clone), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
