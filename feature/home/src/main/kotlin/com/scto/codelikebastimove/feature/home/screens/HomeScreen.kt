package com.scto.codelikebastimove.feature.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.core.resources.R
import com.scto.codelikebastimove.feature.home.navigation.HomeDestination

@Composable
fun HomeScreen(
    onNavigate: (HomeDestination) -> Unit,
    onCreateProject: () -> Unit,
    onImportProject: () -> Unit,
    onOpenProject: () -> Unit,
    onCloneRepository: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.your_ideas_anywhere),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppLogo()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.get_started),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = stringResource(R.string.start_your_project),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeActionButton(
            icon = Icons.Default.Add,
            title = stringResource(R.string.create_project),
            onClick = onCreateProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Outlined.FileDownload,
            title = stringResource(R.string.import_project),
            onClick = onImportProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Folder,
            title = stringResource(R.string.open_existing_project),
            onClick = onOpenProject,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Outlined.CloudDownload,
            title = stringResource(R.string.clone_repository),
            onClick = onCloneRepository,
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Terminal,
            title = stringResource(R.string.console),
            onClick = { onNavigate(HomeDestination.Console) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.settings),
            onClick = { onNavigate(HomeDestination.Settings) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Tune,
            title = stringResource(R.string.ide_configurations),
            onClick = { onNavigate(HomeDestination.Settings) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HomeActionButton(
            icon = Icons.Default.Book,
            title = stringResource(R.string.documentation),
            onClick = { onNavigate(HomeDestination.Documentation) },
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00D9FF),
                        Color(0xFF00B4D8),
                        Color(0xFF7C3AED),
                        Color(0xFFA855F7)
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CLBM",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
            )
            Text(
                text = "</>",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
